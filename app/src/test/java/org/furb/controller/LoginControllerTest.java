package org.furb.controller;

import org.furb.dto.usuario.LoginDTO;
import org.furb.enums.TipoUsuario;
import org.furb.model.Usuario;
import org.furb.security.CookieFactory;
import org.furb.security.JwtService;
import org.furb.services.LoginService;
import org.furb.services.RefreshTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class LoginControllerTest {

    private MockMvc mockMvc;
    private LoginService loginService;
    private JwtService jwtService;
    private RefreshTokenService refreshTokenService;

    @BeforeEach
    void setUp() {
        loginService = mock(LoginService.class);
        jwtService = mock(JwtService.class);
        refreshTokenService = mock(RefreshTokenService.class);
        CookieFactory cookieFactory = new CookieFactory(false, 1_800_000L, 28_800_000L);
        LoginController controller = new LoginController(loginService, jwtService, refreshTokenService, cookieFactory);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void login_credenciaisValidas_seta2CookiesERetorna204() throws Exception {
        Usuario u = new Usuario();
        u.setEmail("a@b.com");
        u.setTipoUsuario(TipoUsuario.CONSUMIDOR);
        when(loginService.autenticar(any(LoginDTO.class))).thenReturn(u);
        when(jwtService.gerarToken("a@b.com", TipoUsuario.CONSUMIDOR)).thenReturn("jwt.fake");
        when(refreshTokenService.emitir(u)).thenReturn("refresh-cru");

        mockMvc.perform(post("/login")
                        .contentType("application/json")
                        .content("{\"email\":\"a@b.com\",\"senha\":\"x\"}"))
                .andExpect(status().isNoContent())
                .andExpect(header().stringValues("Set-Cookie",
                        org.hamcrest.Matchers.hasItem(org.hamcrest.Matchers.containsString("cj_token=jwt.fake"))))
                .andExpect(header().stringValues("Set-Cookie",
                        org.hamcrest.Matchers.hasItem(org.hamcrest.Matchers.containsString("cj_refresh=refresh-cru"))));
    }
}
