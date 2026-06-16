package org.furb.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import org.furb.enums.TipoUsuario;
import org.furb.model.Usuario;
import org.furb.repositories.UsuarioRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock private JwtService jwtService;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private FilterChain chain;

    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        filter = new JwtAuthenticationFilter(jwtService, usuarioRepository);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilter_tokenNoCookie_autentica() throws Exception {
        Usuario u = new Usuario();
        u.setEmail("a@b.com");
        u.setTipoUsuario(TipoUsuario.CONSUMIDOR);
        u.setAtivo(true);
        when(jwtService.validarToken("jwt-cookie")).thenReturn(true);
        when(jwtService.extrairEmail("jwt-cookie")).thenReturn("a@b.com");
        when(usuarioRepository.findByEmail("a@b.com")).thenReturn(Optional.of(u));

        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setServletPath("/produtos");
        req.setCookies(new Cookie("cj_token", "jwt-cookie"));
        MockHttpServletResponse res = new MockHttpServletResponse();

        filter.doFilter(req, res, chain);

        verify(chain).doFilter(req, res);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
    }

    @Test
    void doFilter_semCookie_usaFallbackDoHeader() throws Exception {
        Usuario u = new Usuario();
        u.setEmail("a@b.com");
        u.setTipoUsuario(TipoUsuario.ADMIN);
        u.setAtivo(true);
        when(jwtService.validarToken("jwt-header")).thenReturn(true);
        when(jwtService.extrairEmail("jwt-header")).thenReturn("a@b.com");
        when(usuarioRepository.findByEmail("a@b.com")).thenReturn(Optional.of(u));

        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setServletPath("/produtos");
        req.addHeader("Authorization", "Bearer jwt-header");
        MockHttpServletResponse res = new MockHttpServletResponse();

        filter.doFilter(req, res, chain);

        verify(chain).doFilter(req, res);
    }

    @Test
    void doFilter_semTokenNenhum_responde401() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setServletPath("/produtos");
        MockHttpServletResponse res = new MockHttpServletResponse();

        filter.doFilter(req, res, chain);

        verify(chain, never()).doFilter(any(), any());
        assertThat(res.getStatus()).isEqualTo(401);
    }

    @Test
    void shouldNotFilter_uploadsProdutos_naoEhMaisPulado() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setServletPath("/uploads/produtos/x.jpg");
        req.setMethod("GET");

        assertThat(filter.shouldNotFilter(req)).isFalse();
    }
}
