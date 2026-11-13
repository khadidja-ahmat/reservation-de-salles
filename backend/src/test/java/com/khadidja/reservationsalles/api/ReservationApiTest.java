package com.khadidja.reservationsalles.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.khadidja.reservationsalles.model.Salle;
import com.khadidja.reservationsalles.repository.SalleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Test de bout en bout de l'API (base H2 en mémoire)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ReservationApiTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;
    @Autowired SalleRepository salles;

    // Inscrit un utilisateur et retourne son token
    private String inscrire(String email) throws Exception {
        String corps = "{\"nom\":\"Test\",\"email\":\"" + email + "\",\"motDePasse\":\"secret123\"}";
        String reponse = mvc.perform(post("/api/auth/inscription")
                        .contentType(MediaType.APPLICATION_JSON).content(corps))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        JsonNode noeud = json.readTree(reponse);
        return noeud.get("token").asText();
    }

    @Test
    void sansTokenLesReservationsSontRefusees() throws Exception {
        mvc.perform(get("/api/reservations")).andExpect(status().isUnauthorized());
    }

    @Test
    void unSimpleUtilisateurNePeutPasCreerUneSalle() throws Exception {
        String token = inscrire("simple@test.fr");
        mvc.perform(post("/api/salles")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nom\":\"Nouvelle\",\"capacite\":10,\"equipements\":\"\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void laConnexionVerifieLeMotDePasse() throws Exception {
        inscrire("connexion@test.fr");
        mvc.perform(post("/api/auth/connexion").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"connexion@test.fr\",\"motDePasse\":\"secret123\"}"))
                .andExpect(status().isOk());
        mvc.perform(post("/api/auth/connexion").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"connexion@test.fr\",\"motDePasse\":\"mauvais\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deuxReservationsSurLeMemeCreneauDonnentUnConflit() throws Exception {
        Long salleId = salles.save(new Salle("Salle test", 10, "")).getId();
        String token = inscrire("conflit@test.fr");

        LocalDateTime debut = LocalDateTime.now().plusDays(2).withNano(0);
        String corps = "{\"salleId\":" + salleId + ",\"debut\":\"" + debut + "\",\"fin\":\""
                + debut.plusHours(2) + "\",\"motif\":\"TP\"}";

        // première réservation : acceptée
        mvc.perform(post("/api/reservations").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).content(corps))
                .andExpect(status().isCreated());
        // même créneau : refusé (409)
        mvc.perform(post("/api/reservations").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).content(corps))
                .andExpect(status().isConflict());
    }
}
