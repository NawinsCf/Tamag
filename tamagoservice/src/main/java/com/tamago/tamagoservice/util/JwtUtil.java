package com.tamago.tamagoservice.util;

import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;

@Component
public class JwtUtil {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.exp-minutes:15}")
    private int expMinutes;

    public String generateToken(Map<String, String> claims) {
        Algorithm alg = Algorithm.HMAC256(jwtSecret);
        Date now = new Date();
        Date exp = new Date(now.getTime() + expMinutes * 60L * 1000L);
        com.auth0.jwt.JWTCreator.Builder builder = JWT.create().withIssuedAt(now).withExpiresAt(exp);
        claims.forEach(builder::withClaim);
        return builder.sign(alg);
    }

    public DecodedJWT verify(String token) {
        Algorithm alg = Algorithm.HMAC256(jwtSecret);
        JWTVerifier verifier = JWT.require(alg).build();
        return verifier.verify(token);
    }
}
