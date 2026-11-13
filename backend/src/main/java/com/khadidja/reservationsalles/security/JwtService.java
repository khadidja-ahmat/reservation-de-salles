package com.khadidja.reservationsalles.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/** Crée et lit les jetons JWT. */
@Service
public class JwtService {

    private final SecretKey cle;
    private final long dureeMs;

    public JwtService(@Value("${app.jwt.secret}") String secret,
                      @Value("${app.jwt.duree-heures:8}") long dureeHeures) {
        this.cle = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.dureeMs = dureeHeures * 3600 * 1000;
    }

    public String creerToken(String email, String role) {
        Date maintenant = new Date();
        return Jwts.builder()
                .subject(email)
                .claim("role", role)
                .issuedAt(maintenant)
                .expiration(new Date(maintenant.getTime() + dureeMs))
                .signWith(cle)
                .compact();
    }

    /** Retourne le contenu du token, ou lance une exception s'il est faux ou expiré. */
    public Claims lire(String token) {
        return Jwts.parser().verifyWith(cle).build().parseSignedClaims(token).getPayload();
    }
}
