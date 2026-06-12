package com.aj.personal.projects.management.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.List;

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
        String usernameOrEmail = authentication.getName();

        List<String> roles = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        Date currentDate = new Date();

        Date expiryDate = new Date(currentDate.getTime() + jwtExpirationDate);

        return Jwts.builder()
                .subject(usernameOrEmail)
                .claim("roles", roles)
                .issuedAt(currentDate)
                .expiration(expiryDate)
                .signWith(getPrivateKey(), Jwts.SIG.RS256)
                .compact();
    }



    public String getUsernameFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    public boolean validateToken(String token) {
        try {
            getClaimsFromToken(token);
            return true;
        } catch (JwtException | IllegalArgumentException exception) {
            return false;
        }
    }

    public List<String> getRolesFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("roles", List.class);
    }

    private Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getPublicKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
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
