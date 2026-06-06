package org.furb.security;

import org.furb.model.Usuario;
import org.furb.repositories.UsuarioRepository;
import org.furb.services.exeptions.BusinessException;
import org.furb.services.exeptions.ResourceNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioAutenticadoProviderTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    private UsuarioAutenticadoProvider provider;

    @BeforeEach
    void setUp() {
        provider = new UsuarioAutenticadoProvider(usuarioRepository);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getUsuarioAutenticado_autenticadoEEncontrado_retornaUsuario() {
        Usuario usuario = new Usuario();
        usuario.setEmail("user@x.com");
        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken("user@x.com", null));
        when(usuarioRepository.findByEmail("user@x.com")).thenReturn(Optional.of(usuario));

        Usuario resultado = provider.getUsuarioAutenticado();

        assertThat(resultado).isSameAs(usuario);
    }

    @Test
    void getUsuarioAutenticado_semAutenticacao_lancaBusinessException() {
        SecurityContextHolder.clearContext();

        assertThatThrownBy(() -> provider.getUsuarioAutenticado())
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void getUsuarioAutenticado_autenticadoMasNaoEncontrado_lancaResourceNotFoundException() {
        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken("unknown@x.com", null));
        when(usuarioRepository.findByEmail("unknown@x.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> provider.getUsuarioAutenticado())
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
