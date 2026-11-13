package com.khadidja.reservationsalles.repository;

import com.khadidja.reservationsalles.model.Reservation;
import com.khadidja.reservationsalles.model.StatutReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByUtilisateurEmailOrderByDebutDesc(String email);

    List<Reservation> findAllByOrderByDebutDesc();

    List<Reservation> findBySalleIdAndStatutOrderByDebut(Long salleId, StatutReservation statut);

    /**
     * Compte les réservations confirmées de la salle qui chevauchent [debut, fin[.
     * Deux créneaux se chevauchent si : debutExistant < fin ET finExistant > debut.
     */
    @Query("""
            select count(r) from Reservation r
            where r.salle.id = :salleId
              and r.statut = com.khadidja.reservationsalles.model.StatutReservation.CONFIRMEE
              and r.debut < :fin
              and r.fin > :debut
            """)
    long compterChevauchements(@Param("salleId") Long salleId,
                               @Param("debut") LocalDateTime debut,
                               @Param("fin") LocalDateTime fin);
}
