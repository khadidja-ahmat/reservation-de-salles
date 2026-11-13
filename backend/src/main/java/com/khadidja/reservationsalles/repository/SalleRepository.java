package com.khadidja.reservationsalles.repository;

import com.khadidja.reservationsalles.model.Salle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalleRepository extends JpaRepository<Salle, Long> {

    boolean existsByNom(String nom);
}
