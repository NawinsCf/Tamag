package com.tamago.tamagoservice.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.tamago.tamagoservice.model.RefreshToken;
import com.tamago.tamagoservice.repository.RefreshTokenRepository;

import java.util.UUID;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository repo;

    @Value("${app.refresh-token.exp-days:30}")
    private int refreshTokenExpDays;

    public RefreshTokenService(RefreshTokenRepository repo) {
        this.repo = repo;
    }

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(RefreshTokenService.class);

    public RefreshToken create(Long userId, String tokenHash, String ip, String ua) {
        RefreshToken t = new RefreshToken();
        t.setId(UUID.randomUUID().toString());
        t.setUserId(userId);
        t.setTokenHash(tokenHash);
        t.setIssuedAt(LocalDateTime.now());
        t.setExpiresAt(LocalDateTime.now().plusDays(refreshTokenExpDays));
        t.setIpAddress(ip);
        t.setUserAgent(ua);
        repo.save(t);
        return t;
    }

    public Optional<RefreshToken> findByHash(String hash) {
        return repo.findByTokenHash(hash);
    }

    public void revoke(RefreshToken token) {
        token.setRevoked(true);
        repo.save(token);
    }

    // rotation: mark old replacedBy new one
    public void rotate(RefreshToken oldToken, RefreshToken newToken) {
        oldToken.setRevoked(true);
        oldToken.setReplacedBy(newToken.getId());
        repo.save(oldToken);
        repo.save(newToken);
    }

    public void revokeAllForUser(Long userId) {
        java.util.List<RefreshToken> tokens = repo.findByUserId(userId);
        for (RefreshToken t : tokens) {
            t.setRevoked(true);
        }
        repo.saveAll(tokens);
        logger.warn("Revoked {} refresh tokens for userId={}", tokens.size(), userId);
    }
}
