package com.khadidja.reservationsalles.controller;

import com.khadidja.reservationsalles.dto.Dtos.*;
import com.khadidja.reservationsalles.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService service;

    public ReservationController(ReservationService service) {
        this.service = service;
    }

    @GetMapping
    public List<ReservationResponse> lister(Authentication auth) {
        return service.lister(auth.getName(), estAdmin(auth));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationResponse reserver(Authentication auth, @Valid @RequestBody ReservationRequest requete) {
        return service.reserver(auth.getName(), requete);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void annuler(Authentication auth, @PathVariable Long id) {
        service.annuler(id, auth.getName(), estAdmin(auth));
    }

    private boolean estAdmin(Authentication auth) {
        return auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}
