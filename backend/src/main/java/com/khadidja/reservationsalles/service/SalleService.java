package com.khadidja.reservationsalles.service;

import com.khadidja.reservationsalles.dto.Dtos.*;
import com.khadidja.reservationsalles.exception.ErreurMetier;
import com.khadidja.reservationsalles.model.Salle;
import com.khadidja.reservationsalles.repository.SalleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SalleService {

    private final SalleRepository salles;

    public SalleService(SalleRepository salles) {
        this.salles = salles;
    }

    public List<SalleResponse> lister() {
        return salles.findAll().stream().map(SalleResponse::de).toList();
    }

    public SalleResponse creer(SalleRequest r) {
        if (salles.existsByNom(r.nom())) {
            throw new ErreurMetier(HttpStatus.CONFLICT, "Une salle porte déjà ce nom");
        }
        return SalleResponse.de(salles.save(new Salle(r.nom(), r.capacite(), r.equipements())));
    }

    public SalleResponse modifier(Long id, SalleRequest r) {
        Salle s = trouver(id);
        s.setNom(r.nom());
        s.setCapacite(r.capacite());
        s.setEquipements(r.equipements());
        return SalleResponse.de(salles.save(s));
    }

    public void supprimer(Long id) {
        try {
            salles.delete(trouver(id));
            salles.flush();
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            throw new ErreurMetier(HttpStatus.CONFLICT, "Impossible : cette salle a des réservations");
        }
    }

    private Salle trouver(Long id) {
        return salles.findById(id)
                .orElseThrow(() -> new ErreurMetier(HttpStatus.NOT_FOUND, "Salle introuvable"));
    }
}
