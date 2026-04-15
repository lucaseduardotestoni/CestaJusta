package org.furb.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JwtService {

    private final String secretBase64;

    public JwtService(@Value("${jwt.secret-base64}") String secretBase64) {
        this.secretBase64 = secretBase64;
    }

    private Key getKey() {
        byte[] keyBytes;

        try {
            keyBytes = Decoders.BASE64.decode(secretBase64);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("jwt.secret-base64 invalido: informe um Base64 valido", e);
        }

        if (keyBytes.length < 32) {
            throw new IllegalStateException("jwt.secret-base64 deve ter no minimo 32 bytes apos decode (HS256)");
        }

        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String gerarToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1 dia
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extrairEmail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validarToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}