package com.khadidja.reservationsalles.dto;

import com.khadidja.reservationsalles.model.*;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

/** Tous les objets échangés avec le front (requêtes et réponses) */
public final class Dtos {

    private Dtos() {
    }

    // Authentification 
    public record InscriptionRequest(
            @NotBlank String nom,
            @Email @NotBlank String email,
            @Size(min = 6, message = "6 caractères minimum") String motDePasse) {
    }

    public record ConnexionRequest(@NotBlank String email, @NotBlank String motDePasse) {
    }

    public record AuthResponse(String token, String nom, String email, Role role) {
    }

    // Salles
    public record SalleRequest(
            @NotBlank String nom,
            @Min(1) int capacite,
            String equipements) {
    }

    public record SalleResponse(Long id, String nom, int capacite, String equipements) {
        public static SalleResponse de(Salle s) {
            return new SalleResponse(s.getId(), s.getNom(), s.getCapacite(), s.getEquipements());
        }
    }

    // Réservations
    public record ReservationRequest(
            @NotNull Long salleId,
            @NotNull LocalDateTime debut,
            @NotNull LocalDateTime fin,
            String motif) {
    }

    public record ReservationResponse(Long id, Long salleId, String salleNom, String utilisateurNom,
                                      LocalDateTime debut, LocalDateTime fin, String motif,
                                      StatutReservation statut) {
        public static ReservationResponse de(Reservation r) {
            return new ReservationResponse(r.getId(), r.getSalle().getId(), r.getSalle().getNom(),
                    r.getUtilisateur().getNom(), r.getDebut(), r.getFin(), r.getMotif(), r.getStatut());
        }
    }
}
