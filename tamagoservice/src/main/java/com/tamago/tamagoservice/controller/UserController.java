package com.tamago.tamagoservice.controller;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tamago.tamagoservice.dto.UserCreateRequest;
import com.tamago.tamagoservice.dto.UserResponse;
import com.tamago.tamagoservice.model.User;
import com.tamago.tamagoservice.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    /**
     * Crée un nouvel utilisateur (inscription). Retourne 201 avec les informations publiques
     * de l'utilisateur en cas de succès, ou 400 si les données sont invalides.
     */
    public ResponseEntity<?> create(@Valid @RequestBody UserCreateRequest req) {
        try {
            User created = userService.createUser(req.getPseudo(), req.getMdp(), req.getMail());
            UserResponse resp = new UserResponse(created.getId(), created.getPseudo(), created.getMail(), created.getEstAdmin());
            return ResponseEntity.status(HttpStatus.CREATED).body(resp);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }
}
