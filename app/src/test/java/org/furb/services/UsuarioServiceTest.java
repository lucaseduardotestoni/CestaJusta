package org.furb.services;

import org.furb.dto.usuario.UsuarioCadastroDTO;
import org.furb.dto.usuario.UsuarioResponseDTO;
import org.furb.enums.TipoUsuario;
import org.furb.model.Usuario;
import org.furb.repositories.UsuarioRepository;
import org.furb.services.exeptions.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    private UsuarioCadastroDTO dto;

    @BeforeEach
    void setUp() {
        dto = new UsuarioCadastroDTO();
        dto.setNome("Lucas");
        dto.setEmail("lucas@teste.com");
        dto.setSenha("senha123");
        dto.setTipoUsuario(TipoUsuario.CONSUMIDOR);
    }

    @Test
    void cadastrar_dadosValidos_retornaUsuarioResponseDTO() {
        when(usuarioRepository.existsByEmail("lucas@teste.com")).thenReturn(false);
        when(passwordEncoder.encode("senha123")).thenReturn("$2a$hash");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario u = invocation.getArgument(0);
            u.setDataCriacao(LocalDateTime.now());
            return u;
        });

        UsuarioResponseDTO response = usuarioService.cadastrar(dto);

        assertThat(response.getNome()).isEqualTo("Lucas");
        assertThat(response.getEmail()).isEqualTo("lucas@teste.com");
        assertThat(response.getTipoUsuario()).isEqualTo(TipoUsuario.CONSUMIDOR);
        assertThat(response.getAtivo()).isTrue();
        verify(passwordEncoder).encode("senha123");
    }

    @Test
    void cadastrar_emailJaExistente_lancaBusinessException() {
        when(usuarioRepository.existsByEmail("lucas@teste.com")).thenReturn(true);

        assertThatThrownBy(() -> usuarioService.cadastrar(dto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("e-mail");

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void cadastrar_tipoAdmin_lancaBusinessException() {
        dto.setTipoUsuario(TipoUsuario.ADMIN);
        when(usuarioRepository.existsByEmail("lucas@teste.com")).thenReturn(false);

        assertThatThrownBy(() -> usuarioService.cadastrar(dto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("administrador");

        verify(passwordEncoder, never()).encode(eq("senha123"));
        verify(usuarioRepository, never()).save(any());
    }
}
