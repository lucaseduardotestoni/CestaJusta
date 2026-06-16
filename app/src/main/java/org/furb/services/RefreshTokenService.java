package org.furb.services;

import org.furb.model.RefreshToken;
import org.furb.model.Usuario;
import org.furb.repositories.RefreshTokenRepository;
import org.furb.services.exeptions.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository repository;
    private final long refreshTtlMs;
    private final SecureRandom random = new SecureRandom();

    public RefreshTokenService(RefreshTokenRepository repository,
                               @Value("${app.auth.refresh-ttl-ms:28800000}") long refreshTtlMs) {
        this.repository = repository;
        this.refreshTtlMs = refreshTtlMs;
    }

    public record Rotacao(Usuario usuario, String novoTokenCru) {}

    @Transactional
    public String emitir(Usuario usuario) {
        return emitirNaFamilia(usuario, UUID.randomUUID().toString());
    }

    @Transactional
    public Rotacao rotacionar(String tokenCru) {
        RefreshToken atual = repository.findByTokenHash(hash(tokenCru))
                .orElseThrow(() -> new BusinessException("Refresh token inválido."));

        if (atual.isRevoked()) {
            throw new BusinessException("Refresh token revogado.");
        }
        if (atual.isUsed()) {
            revogarFamilia(atual.getFamilyId());
            throw new BusinessException("Reuso de refresh token detectado; sessão revogada.");
        }
        if (atual.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException("Refresh token expirado.");
        }

        atual.setUsed(true);
        repository.save(atual);

        String novoCru = emitirNaFamilia(atual.getUsuario(), atual.getFamilyId());
        return new Rotacao(atual.getUsuario(), novoCru);
    }

    @Transactional
    public void revogarFamilia(String familyId) {
        List<RefreshToken> familia = repository.findByFamilyId(familyId);
        familia.forEach(t -> t.setRevoked(true));
        repository.saveAll(familia);
    }

    @Transactional
    public void revogarTokenCru(String tokenCru) {
        repository.findByTokenHash(hash(tokenCru))
                .ifPresent(t -> revogarFamilia(t.getFamilyId()));
    }

    private String emitirNaFamilia(Usuario usuario, String familyId) {
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        String cru = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);

        RefreshToken rt = new RefreshToken();
        rt.setUsuario(usuario);
        rt.setTokenHash(hash(cru));
        rt.setFamilyId(familyId);
        rt.setExpiresAt(LocalDateTime.now().plus(Duration.ofMillis(refreshTtlMs)));
        repository.save(rt);
        return cru;
    }

    public String hash(String tokenCru) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(tokenCru.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(64);
            for (byte b : digest) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 indisponível", e);
        }
    }
}
