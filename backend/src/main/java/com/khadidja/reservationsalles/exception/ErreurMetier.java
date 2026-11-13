package com.khadidja.reservationsalles.exception;

import org.springframework.http.HttpStatus;

// Erreur "normale" de l'application (créneau déjà pris, salle introuvable...)
public class ErreurMetier extends RuntimeException {

    private final HttpStatus statut;

    public ErreurMetier(HttpStatus statut, String message) {
        super(message);
        this.statut = statut;
    }

    public HttpStatus getStatut() {
        return statut;
    }
}
