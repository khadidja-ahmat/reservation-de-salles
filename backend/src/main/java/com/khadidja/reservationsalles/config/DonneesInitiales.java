package com.khadidja.reservationsalles.config;

import com.khadidja.reservationsalles.model.Role;
import com.khadidja.reservationsalles.model.Salle;
import com.khadidja.reservationsalles.model.Utilisateur;
import com.khadidja.reservationsalles.repository.SalleRepository;
import com.khadidja.reservationsalles.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

//Au premier démarrage : crée un compte admin et quelques salles d'exemple
@Configuration
@Profile("!test")
public class DonneesInitiales {

    @Bean
    CommandLineRunner initialiser(UtilisateurRepository utilisateurs, SalleRepository salles,
                                  PasswordEncoder encoder,
                                  @Value("${app.admin.email}") String adminEmail,
                                  @Value("${app.admin.mot-de-passe}") String adminMotDePasse) {
        return args -> {
            if (!utilisateurs.existsByEmail(adminEmail)) {
                utilisateurs.save(new Utilisateur("Administrateur", adminEmail,
                        encoder.encode(adminMotDePasse), Role.ADMIN));
            }
            if (salles.count() == 0) {
                salles.save(new Salle("Amphi A", 150, "Micro, Projecteur, Sonorisation"));
                salles.save(new Salle("Amphi B", 80, "Projecteur, Tableau blanc"));
                salles.save(new Salle("Salle TD 101", 30, "Projecteur, Tableau blanc"));
                salles.save(new Salle("Salle TD 102", 30, "Tableau blanc"));
                salles.save(new Salle("Salle TP Informatique 1", 24, "24 postes, Projecteur"));
                salles.save(new Salle("Salle TP Informatique 2", 24, "24 postes, Projecteur"));
                salles.save(new Salle("Salle de réunion R1", 10, "Écran TV, Visioconférence"));
                salles.save(new Salle("Salle de travail en groupe", 6, "Tableau blanc, Prises électriques"));
            }
        };
    }
}
