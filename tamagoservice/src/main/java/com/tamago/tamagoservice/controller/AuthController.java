package com.tamago.tamagoservice.controller;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tamago.tamagoservice.dto.LoginRequest;
import com.tamago.tamagoservice.dto.UserResponse;
import com.tamago.tamagoservice.model.User;
import com.tamago.tamagoservice.service.UserService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
        User u = userService.authenticate(req.getPseudo(), req.getMdp());
        UserResponse resp = new UserResponse(u.getId(), u.getPseudo(), u.getMail(), u.getEstAdmin());
        return ResponseEntity.ok(resp);
    }
}
