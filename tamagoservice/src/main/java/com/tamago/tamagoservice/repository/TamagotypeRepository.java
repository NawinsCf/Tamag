package com.tamago.tamagoservice.repository;

import com.tamago.tamagoservice.model.Tamagotype;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TamagotypeRepository extends JpaRepository<Tamagotype, Long> {
    boolean existsByNom(String nom);
    Optional<Tamagotype> findByNom(String nom);
    java.util.List<Tamagotype> findAllByEstActifTrue();
}
