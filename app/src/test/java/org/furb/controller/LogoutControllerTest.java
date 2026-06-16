package org.furb.controller;

import jakarta.servlet.http.Cookie;
import org.furb.security.CookieFactory;
import org.furb.services.RefreshTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class LogoutControllerTest {

    private MockMvc mockMvc;
    private RefreshTokenService refreshTokenService;

    @BeforeEach
    void setUp() {
        refreshTokenService = mock(RefreshTokenService.class);
        CookieFactory cookieFactory = new CookieFactory(false, 1_800_000L, 28_800_000L);
        LogoutController controller = new LogoutController(refreshTokenService, cookieFactory);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void logout_comCookie_revogaFamiliaELimpaCookies() throws Exception {
        mockMvc.perform(post("/logout").cookie(new Cookie("cj_refresh", "abc")))
                .andExpect(status().isNoContent())
                .andExpect(header().stringValues("Set-Cookie",
                        org.hamcrest.Matchers.hasItem(org.hamcrest.Matchers.containsString("cj_token=;"))))
                .andExpect(header().stringValues("Set-Cookie",
                        org.hamcrest.Matchers.hasItem(org.hamcrest.Matchers.containsString("cj_refresh=;"))));

        verify(refreshTokenService).revogarTokenCru("abc");
    }

    @Test
    void logout_semCookie_aindaLimpaCookiesE204() throws Exception {
        mockMvc.perform(post("/logout"))
                .andExpect(status().isNoContent());
        verify(refreshTokenService, never()).revogarTokenCru(any());
    }
}
