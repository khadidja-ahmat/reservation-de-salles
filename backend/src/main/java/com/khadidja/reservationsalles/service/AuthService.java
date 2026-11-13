package com.khadidja.reservationsalles.service;

import com.khadidja.reservationsalles.dto.Dtos.*;
import com.khadidja.reservationsalles.exception.ErreurMetier;
import com.khadidja.reservationsalles.model.Role;
import com.khadidja.reservationsalles.model.Utilisateur;
import com.khadidja.reservationsalles.repository.UtilisateurRepository;
import com.khadidja.reservationsalles.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UtilisateurRepository utilisateurs;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    public AuthService(UtilisateurRepository utilisateurs, PasswordEncoder encoder, JwtService jwt) {
        this.utilisateurs = utilisateurs;
        this.encoder = encoder;
        this.jwt = jwt;
    }

    public AuthResponse inscrire(InscriptionRequest requete) {
        String email = requete.email().trim().toLowerCase();
        if (utilisateurs.existsByEmail(email)) {
            throw new ErreurMetier(HttpStatus.CONFLICT, "Cet email est déjà utilisé");
        }
        // une inscription crée toujours un simple utilisateur (jamais un admin)
        Utilisateur u = utilisateurs.save(
                new Utilisateur(requete.nom(), email, encoder.encode(requete.motDePasse()), Role.USER));
        return reponse(u);
    }

    public AuthResponse connecter(ConnexionRequest requete) {
        Utilisateur u = utilisateurs.findByEmail(requete.email().trim().toLowerCase())
                .orElseThrow(() -> new ErreurMetier(HttpStatus.UNAUTHORIZED, "Email ou mot de passe incorrect"));
        if (!encoder.matches(requete.motDePasse(), u.getMotDePasse())) {
            throw new ErreurMetier(HttpStatus.UNAUTHORIZED, "Email ou mot de passe incorrect");
        }
        return reponse(u);
    }

    private AuthResponse reponse(Utilisateur u) {
        return new AuthResponse(jwt.creerToken(u.getEmail(), u.getRole().name()),
                u.getNom(), u.getEmail(), u.getRole());
    }
}
