package com.tamago.feedservice.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {

    @Value("${app.jwt.secret:replace_this_with_secure_value}")
    private String jwtSecret;

    @Value("${app.jwt.exp-minutes:15}")
    private int expMinutes;

    public String generateToken(Map<String, String> claims) {
        Instant now = Instant.now();
        Instant exp = now.plus(expMinutes, ChronoUnit.MINUTES);
        Algorithm alg = Algorithm.HMAC256(jwtSecret);
        com.auth0.jwt.JWTCreator.Builder builder = JWT.create().withIssuedAt(Date.from(now)).withExpiresAt(Date.from(exp));
        if (claims != null) {
            claims.forEach(builder::withClaim);
        }
        return builder.sign(alg);
    }

    public DecodedJWT verify(String token) {
        Algorithm alg = Algorithm.HMAC256(jwtSecret);
        JWTVerifier verifier = JWT.require(alg).build();
        return verifier.verify(token);
    }
}
