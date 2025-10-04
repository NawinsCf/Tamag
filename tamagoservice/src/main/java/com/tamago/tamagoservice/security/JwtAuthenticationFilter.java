package com.tamago.tamagoservice.security;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.tamago.tamagoservice.util.JwtUtil;
import com.auth0.jwt.interfaces.DecodedJWT;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Value("${app.cookie.access-name:tmg_at}")
    private String accessCookieName;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie c : cookies) {
                    if (accessCookieName.equals(c.getName())) {
                        String token = c.getValue();
                        try {
                            DecodedJWT jwt = jwtUtil.verify(token);
                            String sub = jwt.getClaim("sub").asString();
                            if (sub != null) {
                                request.setAttribute("authUserId", Long.parseLong(sub));
                            }
                        } catch (Exception e) {
                            // invalid token -> ignore and continue (unauthenticated)
                        }
                        break;
                    }
                }
            }
        } catch (Exception e) {
            // swallow and continue
        }
        filterChain.doFilter(request, response);
    }
}
