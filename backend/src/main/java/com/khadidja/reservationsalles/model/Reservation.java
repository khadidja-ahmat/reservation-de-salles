package com.khadidja.reservationsalles.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservations")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Salle salle;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Utilisateur utilisateur;

    @Column(nullable = false)
    private LocalDateTime debut;

    @Column(nullable = false)
    private LocalDateTime fin;

    private String motif;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutReservation statut = StatutReservation.CONFIRMEE;

    protected Reservation() {
    }

    public Reservation(Salle salle, Utilisateur utilisateur, LocalDateTime debut, LocalDateTime fin, String motif) {
        this.salle = salle;
        this.utilisateur = utilisateur;
        this.debut = debut;
        this.fin = fin;
        this.motif = motif;
    }

    public Long getId() { return id; }
    public Salle getSalle() { return salle; }
    public Utilisateur getUtilisateur() { return utilisateur; }
    public LocalDateTime getDebut() { return debut; }
    public LocalDateTime getFin() { return fin; }
    public String getMotif() { return motif; }
    public StatutReservation getStatut() { return statut; }

    public void annuler() {
        this.statut = StatutReservation.ANNULEE;
    }
}
