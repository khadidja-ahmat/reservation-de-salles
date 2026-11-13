package com.khadidja.reservationsalles.controller;

import com.khadidja.reservationsalles.dto.Dtos.*;
import com.khadidja.reservationsalles.service.ReservationService;
import com.khadidja.reservationsalles.service.SalleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/salles")
public class SalleController {

    private final SalleService salles;
    private final ReservationService reservations;

    public SalleController(SalleService salles, ReservationService reservations) {
        this.salles = salles;
        this.reservations = reservations;
    }

    @GetMapping
    public List<SalleResponse> lister() {
        return salles.lister();
    }

    @GetMapping("/{id}/creneaux")
    public List<ReservationResponse> creneaux(@PathVariable Long id) {
        return reservations.creneauxPris(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SalleResponse creer(@Valid @RequestBody SalleRequest requete) {
        return salles.creer(requete);
    }

    @PutMapping("/{id}")
    public SalleResponse modifier(@PathVariable Long id, @Valid @RequestBody SalleRequest requete) {
        return salles.modifier(id, requete);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void supprimer(@PathVariable Long id) {
        salles.supprimer(id);
    }
}
