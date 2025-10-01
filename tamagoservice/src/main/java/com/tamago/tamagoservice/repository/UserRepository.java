package com.tamago.tamagoservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tamago.tamagoservice.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByPseudo(String pseudo);

    Optional<User> findByPseudo(String pseudo);
}
