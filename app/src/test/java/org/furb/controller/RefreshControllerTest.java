package org.furb.controller;

import jakarta.servlet.http.Cookie;
import org.furb.enums.TipoUsuario;
import org.furb.model.Usuario;
import org.furb.security.CookieFactory;
import org.furb.security.JwtService;
import org.furb.services.RefreshTokenService;
import org.furb.services.exeptions.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class RefreshControllerTest {

    private MockMvc mockMvc;
    private RefreshTokenService refreshTokenService;
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        refreshTokenService = mock(RefreshTokenService.class);
        jwtService = mock(JwtService.class);
        CookieFactory cookieFactory = new CookieFactory(false, 1_800_000L, 28_800_000L);
        RefreshController controller = new RefreshController(refreshTokenService, jwtService, cookieFactory);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void refresh_valido_rotacionaESeta2Cookies() throws Exception {
        Usuario u = new Usuario();
        u.setEmail("a@b.com");
        u.setTipoUsuario(TipoUsuario.CONSUMIDOR);
        when(refreshTokenService.rotacionar("velho"))
                .thenReturn(new RefreshTokenService.Rotacao(u, "novo-refresh"));
        when(jwtService.gerarToken("a@b.com", TipoUsuario.CONSUMIDOR)).thenReturn("novo-jwt");

        mockMvc.perform(post("/refresh").cookie(new Cookie("cj_refresh", "velho")))
                .andExpect(status().isNoContent())
                .andExpect(header().stringValues("Set-Cookie",
                        org.hamcrest.Matchers.hasItem(org.hamcrest.Matchers.containsString("cj_token=novo-jwt"))))
                .andExpect(header().stringValues("Set-Cookie",
                        org.hamcrest.Matchers.hasItem(org.hamcrest.Matchers.containsString("cj_refresh=novo-refresh"))));
    }

    @Test
    void refresh_semCookie_retorna401() throws Exception {
        mockMvc.perform(post("/refresh"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void refresh_invalidoOuReuso_limpaCookiesE401() throws Exception {
        when(refreshTokenService.rotacionar("ruim")).thenThrow(new BusinessException("reuso"));

        mockMvc.perform(post("/refresh").cookie(new Cookie("cj_refresh", "ruim")))
                .andExpect(status().isUnauthorized())
                .andExpect(header().stringValues("Set-Cookie",
                        org.hamcrest.Matchers.hasItem(org.hamcrest.Matchers.containsString("cj_token=;"))));
    }
}
