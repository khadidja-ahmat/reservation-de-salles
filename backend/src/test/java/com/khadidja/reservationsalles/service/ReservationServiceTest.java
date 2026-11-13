package com.khadidja.reservationsalles.service;

import com.khadidja.reservationsalles.dto.Dtos.ReservationRequest;
import com.khadidja.reservationsalles.exception.ErreurMetier;
import com.khadidja.reservationsalles.model.Reservation;
import com.khadidja.reservationsalles.model.Role;
import com.khadidja.reservationsalles.model.Salle;
import com.khadidja.reservationsalles.model.Utilisateur;
import com.khadidja.reservationsalles.repository.ReservationRepository;
import com.khadidja.reservationsalles.repository.SalleRepository;
import com.khadidja.reservationsalles.repository.UtilisateurRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// Tests unitaires de la logique de réservation (sans base de données)
@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock ReservationRepository reservations;
    @Mock SalleRepository salles;
    @Mock UtilisateurRepository utilisateurs;

    ReservationService service;
    Salle salle = new Salle("A101", 30, "Projecteur");
    Utilisateur khadidja = new Utilisateur("Khadidja", "k@test.fr", "x", Role.USER);
    LocalDateTime demain = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0);

    @BeforeEach
    void preparer() {
        service = new ReservationService(reservations, salles, utilisateurs);
    }

    @Test
    void refuseUneFinAvantLeDebut() {
        var requete = new ReservationRequest(1L, demain, demain.minusHours(1), "test");
        ErreurMetier e = assertThrows(ErreurMetier.class, () -> service.reserver("k@test.fr", requete));
        assertEquals(400, e.getStatut().value());
    }

    @Test
    void refuseUneReservationDansLePasse() {
        var requete = new ReservationRequest(1L, LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusDays(1).plusHours(1), "test");
        ErreurMetier e = assertThrows(ErreurMetier.class, () -> service.reserver("k@test.fr", requete));
        assertEquals(400, e.getStatut().value());
    }

    @Test
    void refuseUnCreneauDejaPris() {
        when(salles.findById(1L)).thenReturn(Optional.of(salle));
        when(utilisateurs.findByEmail("k@test.fr")).thenReturn(Optional.of(khadidja));
        when(reservations.compterChevauchements(any(), any(), any())).thenReturn(1L);

        var requete = new ReservationRequest(1L, demain, demain.plusHours(2), "réunion");
        ErreurMetier e = assertThrows(ErreurMetier.class, () -> service.reserver("k@test.fr", requete));

        assertEquals(409, e.getStatut().value());
        verify(reservations, never()).save(any());
    }

    @Test
    void enregistreUnCreneauLibre() {
        when(salles.findById(1L)).thenReturn(Optional.of(salle));
        when(utilisateurs.findByEmail("k@test.fr")).thenReturn(Optional.of(khadidja));
        when(reservations.compterChevauchements(any(), any(), any())).thenReturn(0L);
        when(reservations.save(any(Reservation.class))).thenAnswer(i -> i.getArgument(0));

        var reponse = service.reserver("k@test.fr", new ReservationRequest(1L, demain, demain.plusHours(2), "TP"));

        assertEquals("A101", reponse.salleNom());
        verify(reservations).save(any(Reservation.class));
    }

    @Test
    void unUtilisateurNePeutPasAnnulerLaReservationDunAutre() {
        Utilisateur autre = new Utilisateur("Autre", "autre@test.fr", "x", Role.USER);
        Reservation r = new Reservation(salle, autre, demain, demain.plusHours(1), "x");
        when(reservations.findById(5L)).thenReturn(Optional.of(r));

        ErreurMetier e = assertThrows(ErreurMetier.class, () -> service.annuler(5L, "k@test.fr", false));
        assertEquals(403, e.getStatut().value());
    }

    @Test
    void unAdminPeutToutAnnuler() {
        Utilisateur autre = new Utilisateur("Autre", "autre@test.fr", "x", Role.USER);
        Reservation r = new Reservation(salle, autre, demain, demain.plusHours(1), "x");
        when(reservations.findById(5L)).thenReturn(Optional.of(r));

        service.annuler(5L, "admin@test.fr", true);

        assertEquals("ANNULEE", r.getStatut().name());
    }
}
