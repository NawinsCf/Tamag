package com.tamago.feedservice.repository;

import com.tamago.feedservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
