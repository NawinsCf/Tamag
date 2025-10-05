package com.tamago.feedservice.security;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.tamago.feedservice.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtUtil jwtUtil;

    @Value("${app.cookie.access-name:tmg_at}")
    private String accessCookieName;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie c : cookies) {
                    if (accessCookieName.equals(c.getName())) {
                        String token = c.getValue();
                        if (token != null && !token.isEmpty()) {
                            DecodedJWT jwt = jwtUtil.verify(token);
                            String sub = jwt.getClaim("sub").asString();
                            request.setAttribute("authUserId", Long.parseLong(sub));
                        }
                        break;
                    }
                }
            }
        } catch (Exception ex) {
            log.debug("Failed to validate JWT: {}", ex.getMessage());
        }
        filterChain.doFilter(request, response);
    }
}
