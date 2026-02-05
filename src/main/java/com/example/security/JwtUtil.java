package com.example.security;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;
import java.util.List;
@Component
public class JwtUtil {
    private final String SECRET_KEY = "12345678901234567890123456789012";
    private final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

public String generateToken(String username, List<String> roles) {
    return Jwts.builder()
            .setSubject(username)
            .claim("roles", roles) 
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + 86400000))
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();
}

    public String extractUsername(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody().getSubject();
    }
}