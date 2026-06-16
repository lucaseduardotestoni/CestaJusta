package org.furb.services;

import org.furb.enums.TipoUsuario;
import org.furb.model.RefreshToken;
import org.furb.model.Usuario;
import org.furb.repositories.RefreshTokenRepository;
import org.furb.services.exeptions.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository repository;

    private RefreshTokenService service;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        service = new RefreshTokenService(repository, 28_800_000L); // 8h
        usuario = new Usuario();
        usuario.setEmail("user@x.com");
        usuario.setTipoUsuario(TipoUsuario.CONSUMIDOR);
        usuario.setAtivo(true);
    }

    @Test
    void emitir_persisteHashEFamilia_eRetornaTokenCru() {
        when(repository.save(any(RefreshToken.class))).thenAnswer(inv -> inv.getArgument(0));

        String raw = service.emitir(usuario);

        assertThat(raw).isNotBlank();
        ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);
        verify(repository).save(captor.capture());
        RefreshToken salvo = captor.getValue();
        assertThat(salvo.getTokenHash()).isNotEqualTo(raw);
        assertThat(salvo.getTokenHash()).hasSize(64);
        assertThat(salvo.getFamilyId()).isNotBlank();
        assertThat(salvo.getUsuario()).isSameAs(usuario);
        assertThat(salvo.getExpiresAt()).isAfter(LocalDateTime.now());
    }

    @Test
    void rotacionar_tokenValido_marcaUsadoEEmiteNovoNaMesmaFamilia() {
        String raw = "token-cru-valido";
        RefreshToken atual = novoToken(raw, "fam-1", false, false, LocalDateTime.now().plusHours(8));
        when(repository.findByTokenHash(service.hash(raw))).thenReturn(Optional.of(atual));
        when(repository.save(any(RefreshToken.class))).thenAnswer(inv -> inv.getArgument(0));

        RefreshTokenService.Rotacao r = service.rotacionar(raw);

        assertThat(atual.isUsed()).isTrue();
        assertThat(r.usuario()).isSameAs(usuario);
        assertThat(r.novoTokenCru()).isNotBlank().isNotEqualTo(raw);
        ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);
        verify(repository, atLeast(1)).save(captor.capture());
        RefreshToken novo = captor.getAllValues().get(captor.getAllValues().size() - 1);
        assertThat(novo.getFamilyId()).isEqualTo("fam-1");
    }

    @Test
    void rotacionar_tokenJaUsado_revogaFamiliaInteira_eLanca() {
        String raw = "token-reusado";
        RefreshToken usado = novoToken(raw, "fam-2", true, false, LocalDateTime.now().plusHours(8));
        RefreshToken irmao = novoToken("outro", "fam-2", false, false, LocalDateTime.now().plusHours(8));
        when(repository.findByTokenHash(service.hash(raw))).thenReturn(Optional.of(usado));
        when(repository.findByFamilyId("fam-2")).thenReturn(List.of(usado, irmao));

        assertThatThrownBy(() -> service.rotacionar(raw)).isInstanceOf(BusinessException.class);

        assertThat(usado.isRevoked()).isTrue();
        assertThat(irmao.isRevoked()).isTrue();
        verify(repository).saveAll(anyList());
    }

    @Test
    void rotacionar_tokenExpirado_lanca() {
        String raw = "token-expirado";
        RefreshToken exp = novoToken(raw, "fam-3", false, false, LocalDateTime.now().minusMinutes(1));
        when(repository.findByTokenHash(service.hash(raw))).thenReturn(Optional.of(exp));

        assertThatThrownBy(() -> service.rotacionar(raw)).isInstanceOf(BusinessException.class);
    }

    @Test
    void rotacionar_tokenInexistente_lanca() {
        when(repository.findByTokenHash(any())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.rotacionar("qualquer")).isInstanceOf(BusinessException.class);
    }

    private RefreshToken novoToken(String raw, String fam, boolean used, boolean revoked, LocalDateTime exp) {
        RefreshToken t = new RefreshToken();
        t.setUsuario(usuario);
        t.setTokenHash(service.hash(raw));
        t.setFamilyId(fam);
        t.setUsed(used);
        t.setRevoked(revoked);
        t.setExpiresAt(exp);
        return t;
    }
}
