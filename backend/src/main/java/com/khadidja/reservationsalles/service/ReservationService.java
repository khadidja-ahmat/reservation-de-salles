package com.khadidja.reservationsalles.service;

import com.khadidja.reservationsalles.dto.Dtos.*;
import com.khadidja.reservationsalles.exception.ErreurMetier;
import com.khadidja.reservationsalles.model.*;
import com.khadidja.reservationsalles.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservationService {

    private final ReservationRepository reservations;
    private final SalleRepository salles;
    private final UtilisateurRepository utilisateurs;

    public ReservationService(ReservationRepository reservations, SalleRepository salles,
                              UtilisateurRepository utilisateurs) {
        this.reservations = reservations;
        this.salles = salles;
        this.utilisateurs = utilisateurs;
    }

    // Réserve une salle, après avoir vérifié les dates et que le créneau est libre
    public ReservationResponse reserver(String emailUtilisateur, ReservationRequest r) {
        verifierDates(r.debut(), r.fin());

        Salle salle = salles.findById(r.salleId())
                .orElseThrow(() -> new ErreurMetier(HttpStatus.NOT_FOUND, "Salle introuvable"));
        Utilisateur utilisateur = utilisateurs.findByEmail(emailUtilisateur)
                .orElseThrow(() -> new ErreurMetier(HttpStatus.UNAUTHORIZED, "Utilisateur inconnu"));

        if (reservations.compterChevauchements(salle.getId(), r.debut(), r.fin()) > 0) {
            throw new ErreurMetier(HttpStatus.CONFLICT, "Ce créneau est déjà réservé pour cette salle");
        }
        Reservation enregistree = reservations.save(
                new Reservation(salle, utilisateur, r.debut(), r.fin(), r.motif()));
        return ReservationResponse.de(enregistree);
    }

    // Un admin voit tout, un utilisateur voit seulement ses réservations
    public List<ReservationResponse> lister(String email, boolean estAdmin) {
        List<Reservation> liste = estAdmin
                ? reservations.findAllByOrderByDebutDesc()
                : reservations.findByUtilisateurEmailOrderByDebutDesc(email);
        return liste.stream().map(ReservationResponse::de).toList();
    }

    // Créneaux déjà pris d'une salle (pour afficher les disponibilités)
    public List<ReservationResponse> creneauxPris(Long salleId) {
        return reservations.findBySalleIdAndStatutOrderByDebut(salleId, StatutReservation.CONFIRMEE)
                .stream().map(ReservationResponse::de).toList();
    }

    public void annuler(Long id, String email, boolean estAdmin) {
        Reservation r = reservations.findById(id)
                .orElseThrow(() -> new ErreurMetier(HttpStatus.NOT_FOUND, "Réservation introuvable"));
        // on ne peut annuler que ses propres réservations (sauf admin)
        if (!estAdmin && !r.getUtilisateur().getEmail().equals(email)) {
            throw new ErreurMetier(HttpStatus.FORBIDDEN, "Ce n'est pas votre réservation");
        }
        r.annuler();
        reservations.save(r);
    }

    private void verifierDates(LocalDateTime debut, LocalDateTime fin) {
        if (!fin.isAfter(debut)) {
            throw new ErreurMetier(HttpStatus.BAD_REQUEST, "La fin doit être après le début");
        }
        if (debut.isBefore(LocalDateTime.now())) {
            throw new ErreurMetier(HttpStatus.BAD_REQUEST, "Impossible de réserver dans le passé");
        }
    }
}
