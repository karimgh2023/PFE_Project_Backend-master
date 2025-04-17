package com.PFE.DTT.security;

import com.PFE.DTT.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    public int extractUserId(String token) {
        Claims claims = extractClaims(token);
        if (claims.get("userId") == null) {
            throw new RuntimeException("User ID not found in JWT");
        }
        return Integer.parseInt(claims.get("userId").toString());
    }

    public String generateToken(User user) {
        // Build a custom map of safe user data
        var userData = new java.util.HashMap<String, Object>();
        userData.put("id", user.getId());
        userData.put("email", user.getEmail());
        userData.put("firstName", user.getFirstName());
        userData.put("lastName", user.getLastName());
        userData.put("role", user.getRole().name());
        userData.put("department", user.getDepartment() != null ? user.getDepartment().getName() : null);
        userData.put("phone", user.getPhoneNumber());

        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("user", userData) // ✅ All allowed user fields// Still included for easy access
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS512, secret.getBytes())
                .compact();
    }

    public Claims extractClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secret.getBytes())  // ✅ Ensure getBytes() is used here
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }

    public String extractRole(String token) {
        return extractClaims(token).get("role", String.class);  // ✅ Extracts role safely
    }

    public boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }
}
