package org.furb.services;

import org.furb.dto.usuario.LoginDTO;
import org.furb.enums.TipoUsuario;
import org.furb.model.Usuario;
import org.furb.repositories.UsuarioRepository;
import org.furb.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private LoginService loginService;

    private LoginDTO dto;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        dto = new LoginDTO();
        dto.setEmail("lucas@teste.com");
        dto.setSenha("senha123");

        usuario = new Usuario();
        usuario.setEmail("lucas@teste.com");
        usuario.setSenha("$2a$hashSalvo");
        usuario.setTipoUsuario(TipoUsuario.CONSUMIDOR);
        usuario.setAtivo(true);
    }

    @Test
    void login_credenciaisValidas_retornaToken() {
        when(usuarioRepository.findByEmail("lucas@teste.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("senha123", "$2a$hashSalvo")).thenReturn(true);
        when(jwtService.gerarToken("lucas@teste.com", TipoUsuario.CONSUMIDOR)).thenReturn("token.jwt.fake");

        String token = loginService.login(dto);

        assertThat(token).isEqualTo("token.jwt.fake");
    }

    @Test
    void login_emailNaoEncontrado_lancaUnauthorized() {
        when(usuarioRepository.findByEmail("lucas@teste.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> loginService.login(dto))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("statusCode", HttpStatus.UNAUTHORIZED);
    }

    @Test
    void login_senhaInvalida_lancaUnauthorized() {
        when(usuarioRepository.findByEmail("lucas@teste.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("senha123", "$2a$hashSalvo")).thenReturn(false);

        assertThatThrownBy(() -> loginService.login(dto))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("statusCode", HttpStatus.UNAUTHORIZED);
    }
}