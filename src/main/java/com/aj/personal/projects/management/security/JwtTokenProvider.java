package com.aj.personal.projects.management.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${app.jwt.private-key}")
    private String jwtPrivateKey;

    @Value("${app.jwt.public-key}")
    private String jwtPublicKey;

    @Value("${app.jwt-expiration-milliseconds}")
    private Long jwtExpirationDate;

    // Generate jwt token
    public String generateToken(Authentication authentication) {
        String username = authentication.getName();

        Date currentDate = new Date();

        Date expirtyDate = new Date(currentDate.getTime() + jwtExpirationDate);

        return Jwts.builder()
                .subject(username)
                .issuedAt(currentDate)
                .expiration(expirtyDate)
                .signWith(getPrivateKey(), Jwts.SIG.RS256)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getPublicKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }

    public boolean validateToken(String token) {
        Jwts.parser()
                .verifyWith(getPublicKey())
                .build()
                .parseSignedClaims(token);


        return true;
    }

    private PrivateKey getPrivateKey() {
        try {
            byte[] pemBytes = Base64.getDecoder().decode(jwtPrivateKey);

            String privateKeyPem = new String(pemBytes)
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s", "");

            byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyPem);

            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            return keyFactory.generatePrivate(keySpec);
        } catch (Exception exception) {
            throw new RuntimeException("Failed to load RSA private key", exception);
        }
    }

    private PublicKey getPublicKey() {
        try {
            byte[] pemBytes = Base64.getDecoder().decode(jwtPublicKey);

            String publicKeyPem = new String(pemBytes)
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s", "");


            byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyPem);

            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            return keyFactory.generatePublic(keySpec);

        } catch (Exception exception) {
            throw new RuntimeException("Failed to load RSA public key", exception);
        }
    }

}
