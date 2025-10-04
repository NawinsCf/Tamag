package com.tamago.tamagoservice.controller;

import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.beans.factory.annotation.Value;

import com.tamago.tamagoservice.dto.LoginRequest;
import com.tamago.tamagoservice.dto.UserResponse;
import com.tamago.tamagoservice.model.User;
import com.tamago.tamagoservice.model.RefreshToken;
import com.tamago.tamagoservice.service.UserService;
import com.tamago.tamagoservice.service.RefreshTokenService;
import com.tamago.tamagoservice.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final JwtUtil jwtUtil;

    @Value("${app.cookie.access-name:tmg_at}")
    private String accessCookieName;

    @Value("${app.cookie.refresh-name:tmg_rt}")
    private String refreshCookieName;

    @Value("${app.cookie.secure:false}")
    private boolean cookieSecure;

    @Value("${app.cookie.same-site:Lax}")
    private String cookieSameSite;

    @Value("${app.cookie.domain:}")
    private String cookieDomain;

    @Value("${app.cookie.path:/}")
    private String cookiePath;

    @Value("${app.jwt.exp-minutes:15}")
    private int jwtExpMinutes;

    @Value("${app.refresh-token.exp-days:30}")
    private int refreshTokenExpDays;

    public AuthController(UserService userService, RefreshTokenService refreshTokenService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
        this.jwtUtil = jwtUtil;
    }

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req, HttpServletRequest request, HttpServletResponse response) {
        User u = userService.authenticate(req.getPseudo(), req.getMdp());

        // generate access token
        String jti = UUID.randomUUID().toString();
        String access = jwtUtil.generateToken(Map.of(
            "sub", String.valueOf(u.getId()),
            "pseudo", u.getPseudo(),
            "estAdmin", String.valueOf(Boolean.TRUE.equals(u.getEstAdmin())),
            "jti", jti
        ));

        // create opaque refresh token and store hashed
        String refreshPlain = UUID.randomUUID().toString();
        String refreshHash = sha256Hex(refreshPlain);
        String ip = request.getRemoteAddr();
        String ua = request.getHeader("User-Agent");
        RefreshToken rt = refreshTokenService.create(u.getId(), refreshHash, ip, ua);

        // set cookies
        ResponseCookie atCookie = ResponseCookie.from(accessCookieName, access)
            .httpOnly(true)
            .secure(cookieSecure)
            .path(cookiePath)
            .sameSite(cookieSameSite)
            .maxAge(Duration.ofMinutes(jwtExpMinutes))
            .build();

        ResponseCookie rtCookie = ResponseCookie.from(refreshCookieName, refreshPlain)
            .httpOnly(true)
            .secure(cookieSecure)
            .path(cookiePath)
            .sameSite(cookieSameSite)
            .maxAge(Duration.ofDays(refreshTokenExpDays))
            .build();

        response.addHeader("Set-Cookie", atCookie.toString());
        response.addHeader("Set-Cookie", rtCookie.toString());

        UserResponse resp = new UserResponse(u.getId(), u.getPseudo(), u.getMail(), u.getEstAdmin());
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@CookieValue(name = "tmg_rt", required = false) String refreshCookie, HttpServletRequest request, HttpServletResponse response) {
        if (refreshCookie == null || refreshCookie.isBlank()) {
            return ResponseEntity.status(401).build();
        }
        String receivedHash = sha256Hex(refreshCookie);
        Optional<RefreshToken> found = refreshTokenService.findByHash(receivedHash);
        if (found.isEmpty()) {
            // token not found -> possible reuse or invalid
            return ResponseEntity.status(401).build();
        }
        RefreshToken token = found.get();
        // If token is revoked or expired -> unauthorized
        if (Boolean.TRUE.equals(token.getRevoked()) || token.getExpiresAt().isBefore(LocalDateTime.now())) {
            // possible reuse detection: token is revoked or expired but presented -> replay attempt
            String ip = request.getRemoteAddr();
            String ua = request.getHeader("User-Agent");
            String tid = token.getId();
            logger.warn("Detected reuse of revoked/expired refresh token for userId={} tokenId={} ip={} ua={}", token.getUserId(), tid, ip, ua);
            refreshTokenService.revokeAllForUser(token.getUserId());
            return ResponseEntity.status(401).build();
        }

        // If token has already been rotated (replacedBy not null), it's a replay/reuse attempt
        if (token.getReplacedBy() != null && !token.getReplacedBy().isBlank()) {
            // token has been rotated previously -> replay/reuse
            String ip = request.getRemoteAddr();
            String ua = request.getHeader("User-Agent");
            logger.warn("Detected reuse of rotated refresh token for userId={} tokenId={} replacedBy={} ip={} ua={}", token.getUserId(), token.getId(), token.getReplacedBy(), ip, ua);
            refreshTokenService.revokeAllForUser(token.getUserId());
            return ResponseEntity.status(401).build();
        }

        // issue new tokens
        Optional<User> optUser = Optional.empty();
        // load user id from token
        Long uid = token.getUserId();
        // here we use userService repository indirectly
        try {
            // generate access JWT
            String jti = UUID.randomUUID().toString();
            User u = new User();
            u.setId(uid);
            // minimal payload — fetch real user if needed
            String access = jwtUtil.generateToken(Map.of(
                "sub", String.valueOf(uid),
                "jti", jti
            ));

            // create rotation refresh token
            String newRefreshPlain = UUID.randomUUID().toString();
            String newHash = sha256Hex(newRefreshPlain);
            String ip = request.getRemoteAddr();
            String ua = request.getHeader("User-Agent");
            RefreshToken newRt = refreshTokenService.create(uid, newHash, ip, ua);
            refreshTokenService.rotate(token, newRt);

            ResponseCookie atCookie = ResponseCookie.from(accessCookieName, access)
                .httpOnly(true)
                .secure(cookieSecure)
                .path(cookiePath)
                .sameSite(cookieSameSite)
                .maxAge(Duration.ofMinutes(jwtExpMinutes))
                .build();

            ResponseCookie rtCookie = ResponseCookie.from(refreshCookieName, newRefreshPlain)
                .httpOnly(true)
                .secure(cookieSecure)
                .path(cookiePath)
                .sameSite(cookieSameSite)
                .maxAge(Duration.ofDays(refreshTokenExpDays))
                .build();

            response.addHeader("Set-Cookie", atCookie.toString());
            response.addHeader("Set-Cookie", rtCookie.toString());

            return ResponseEntity.ok().build();
        } catch (Exception ex) {
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@CookieValue(name = "tmg_rt", required = false) String refreshCookie, HttpServletRequest request, HttpServletResponse response) {
        if (refreshCookie != null && !refreshCookie.isBlank()) {
            String h = sha256Hex(refreshCookie);
            Optional<RefreshToken> f = refreshTokenService.findByHash(h);
            f.ifPresent(t -> refreshTokenService.revoke(t));
        }

        // clear cookies by setting empty value and maxAge=0
        ResponseCookie atClear = ResponseCookie.from(accessCookieName, "")
            .httpOnly(true)
            .secure(cookieSecure)
            .path(cookiePath)
            .sameSite(cookieSameSite)
            .maxAge(Duration.ZERO)
            .build();
        ResponseCookie rtClear = ResponseCookie.from(refreshCookieName, "")
            .httpOnly(true)
            .secure(cookieSecure)
            .path(cookiePath)
            .sameSite(cookieSameSite)
            .maxAge(Duration.ZERO)
            .build();
        response.addHeader("Set-Cookie", atClear.toString());
        response.addHeader("Set-Cookie", rtClear.toString());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(HttpServletRequest request) {
        Object o = request.getAttribute("authUserId");
        if (o == null) return ResponseEntity.status(401).build();
        Long id = (Long) o;
        Optional<User> u = userService.getUserById(id);
        if (u.isEmpty()) return ResponseEntity.status(401).build();
        User user = u.get();
        UserResponse resp = new UserResponse(user.getId(), user.getPseudo(), user.getMail(), user.getEstAdmin());
        return ResponseEntity.ok(resp);
    }

    private String sha256Hex(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(s.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }
    }
}
