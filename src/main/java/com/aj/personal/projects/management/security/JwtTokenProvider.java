package com.aj.personal.projects.management.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

    @Value("${app.jwt.private-key}")
    private String jwtPrivateKey;

    @Value("${app.jwt.public-key}")
    private String jwtPublicKey;

    @Value("${app.jwt.access-token-expiration-milliseconds}")
    private Long accessTokenExpiration;

    @Value("${app.jwt.refresh-token-expiration-milliseconds}")
    private Long refreshTokenExpiration;

    private volatile PrivateKey privateKey;
    private volatile PublicKey publicKey;

    public String generateAccessToken(Authentication authentication, String sessionId) {
        String usernameOrEmail = authentication.getName();

        List<String> roles = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        Date currentDate = new Date();
        Date expiryDate = new Date(currentDate.getTime() + accessTokenExpiration);

        return Jwts.builder()
                .subject(usernameOrEmail)
                .claim("type", "access")
                .claim("sid", sessionId)
                .claim("roles", roles)
                .issuedAt(currentDate)
                .expiration(expiryDate)
                .signWith(getPrivateKey(), Jwts.SIG.RS256)
                .compact();
    }

    public String generateRefreshToken(
            Authentication authentication,
            String sessionId,
            Instant expiresAt
    ) {
        String usernameOrEmail = authentication.getName();
        Date currentDate = new Date();
        Date expiryDate = Date.from(expiresAt);

        return Jwts.builder()
                .subject(usernameOrEmail)
                .claim("type", "refresh")
                .claim("sid", sessionId)
                .issuedAt(currentDate)
                .expiration(expiryDate)
                .signWith(getPrivateKey(), Jwts.SIG.RS256)
                .compact();
    }

    public long getAccessTokenExpirationSeconds() {
        return accessTokenExpiration / 1000;
    }

    public long getRefreshTokenExpirationSeconds() {
        return refreshTokenExpiration / 1000;
    }

    public long getRefreshTokenExpirationMilliseconds() {
        return refreshTokenExpiration;
    }

    public String getUsernameFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    public String getSessionIdFromToken(String token) {
        return getClaimsFromToken(token).get("sid", String.class);
    }

    public List<String> getRolesFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        List<?> roles = claims.get("roles", List.class);

        if (roles == null) {
            return List.of();
        }

        List<String> resolvedRoles = new ArrayList<>();
        for (Object role : roles) {
            resolvedRoles.add(String.valueOf(role));
        }
        return resolvedRoles;
    }

    public boolean validateAccessToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return "access".equals(claims.get("type", String.class));
        } catch (JwtException | IllegalArgumentException exception) {
            return false;
        }
    }

    public boolean validateRefreshToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return "refresh".equals(claims.get("type", String.class));
        } catch (JwtException | IllegalArgumentException exception) {
            return false;
        }
    }

    private Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getPublicKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private PrivateKey getPrivateKey() {
        if (privateKey != null) {
            return privateKey;
        }

        try {
            byte[] pemBytes = Base64.getDecoder().decode(jwtPrivateKey);
            String privateKeyPem = new String(pemBytes)
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s", "");

            byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyPem);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            privateKey = keyFactory.generatePrivate(keySpec);
            return privateKey;
        } catch (Exception exception) {
            throw new RuntimeException("Failed to load RSA private key", exception);
        }
    }

    private PublicKey getPublicKey() {
        if (publicKey != null) {
            return publicKey;
        }

        try {
            byte[] pemBytes = Base64.getDecoder().decode(jwtPublicKey);
            String publicKeyPem = new String(pemBytes)
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s", "");

            byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyPem);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            publicKey = keyFactory.generatePublic(keySpec);
            return publicKey;
        } catch (Exception exception) {
            throw new RuntimeException("Failed to load RSA public key", exception);
        }
    }
}
