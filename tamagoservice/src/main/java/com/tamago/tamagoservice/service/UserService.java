package com.tamago.tamagoservice.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tamago.tamagoservice.exception.DuplicateResourceException;
import com.tamago.tamagoservice.model.User;
import com.tamago.tamagoservice.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User createUser(String pseudo, String rawPassword, String mail) {
        // quick pre-check (gives fast feedback most of the time)
        if (userRepository.existsByPseudo(pseudo)) {
            throw new DuplicateResourceException("Pseudo already in use");
        }
        // rawPassword is expected to be the client-side SHA-256 hex of the user's password.
        // We protect stored credentials by applying bcrypt to that client-side hash.
        String hashed = passwordEncoder.encode(rawPassword);
        User u = new User();
        u.setPseudo(pseudo);
        u.setMdp(hashed);
        u.setMail(mail);
        u.setEstAdmin(Boolean.FALSE);

        try {
            return userRepository.save(u);
        } catch (DataIntegrityViolationException ex) {
            // This covers the race / DB-level uniqueness violation
            throw new DuplicateResourceException("Pseudo already in use", ex);
        }
    }

    public User authenticate(String pseudo, String rawPassword) {
        // rawPassword is expected to be the client-side SHA-256 hex of the user's password.
        return userRepository.findByPseudo(pseudo)
                .map(u -> {
                    if (passwordEncoder.matches(rawPassword, u.getMdp())) {
                        return u;
                    } else {
                        throw new com.tamago.tamagoservice.exception.AuthenticationFailedException("Invalid credentials");
                    }
                }).orElseThrow(() -> new com.tamago.tamagoservice.exception.AuthenticationFailedException("Invalid credentials"));
    }

    public java.util.Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
}
