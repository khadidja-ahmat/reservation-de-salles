package com.khadidja.reservationsalles.model;

import jakarta.persistence.*;

@Entity
@Table(name = "salles")
public class Salle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nom;

    @Column(nullable = false)
    private int capacite;

    // équipements séparés par des virgules (ex : "Projecteur, Tableau")
    private String equipements;

    protected Salle() {
    }

    public Salle(String nom, int capacite, String equipements) {
        this.nom = nom;
        this.capacite = capacite;
        this.equipements = equipements;
    }

    public Long getId() { return id; }
    public String getNom() { return nom; }
    public int getCapacite() { return capacite; }
    public String getEquipements() { return equipements; }

    public void setNom(String nom) { this.nom = nom; }
    public void setCapacite(int capacite) { this.capacite = capacite; }
    public void setEquipements(String equipements) { this.equipements = equipements; }
}
