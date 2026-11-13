package com.khadidja.reservationsalles.controller;

import com.khadidja.reservationsalles.dto.Dtos.*;
import com.khadidja.reservationsalles.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService auth;

    public AuthController(AuthService auth) {
        this.auth = auth;
    }

    @PostMapping("/inscription")
    public AuthResponse inscription(@Valid @RequestBody InscriptionRequest requete) {
        return auth.inscrire(requete);
    }

    @PostMapping("/connexion")
    public AuthResponse connexion(@Valid @RequestBody ConnexionRequest requete) {
        return auth.connecter(requete);
    }
}
