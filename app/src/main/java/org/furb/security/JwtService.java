package org.furb.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.furb.enums.TipoUsuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JwtService {

    private final String secretBase64;
    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

    public JwtService(@Value("${jwt.secret-base64}") String secretBase64) {
        this.secretBase64 = secretBase64;
        logger.debug("JwtService inicializado. Secret Base64 configurado: {} bytes após decode",
            Decoders.BASE64.decode(secretBase64).length);
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
        return gerarToken(email, null);
    }

    public String gerarToken(String email, TipoUsuario tipo) {
        JwtBuilder builder = Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1 dia
                .signWith(getKey(), SignatureAlgorithm.HS256);
        if (tipo != null) {
            builder.claim("tipo", tipo.name());
        }
        return builder.compact();
    }

    public String extrairTipo(String token) {
        Object tipo = Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("tipo");
        return tipo != null ? tipo.toString() : null;
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
            logger.debug("Token validado com sucesso");
            return true;
        } catch (ExpiredJwtException e) {
            logger.warn("Token expirado");
            return false;
        } catch (SignatureException e) {
            logger.warn("Assinatura do token inválida - provavelmente JWT_SECRET diferente");
            return false;
        } catch (JwtException e) {
            logger.warn("Erro ao validar JWT: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            logger.error("Erro inesperado ao validar token", e);
            return false;
        }
    }
}