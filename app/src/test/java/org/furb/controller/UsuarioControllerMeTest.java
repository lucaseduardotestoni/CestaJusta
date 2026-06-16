package org.furb.controller;

import org.furb.enums.TipoUsuario;
import org.furb.model.Usuario;
import org.furb.security.UsuarioAutenticadoProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UsuarioControllerMeTest {

    private MockMvc mockMvc;
    private UsuarioAutenticadoProvider provider;

    @BeforeEach
    void setUp() {
        org.furb.services.UsuarioService usuarioService = mock(org.furb.services.UsuarioService.class);
        provider = mock(UsuarioAutenticadoProvider.class);
        UsuarioController controller = new UsuarioController(usuarioService, provider);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void me_retornaNomeEmailTipo() throws Exception {
        Usuario u = new Usuario();
        u.setNome("Admin");
        u.setEmail("admin@x.com");
        u.setTipoUsuario(TipoUsuario.ADMIN);
        when(provider.getUsuarioAutenticado()).thenReturn(u);

        mockMvc.perform(get("/usuarios/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Admin"))
                .andExpect(jsonPath("$.email").value("admin@x.com"))
                .andExpect(jsonPath("$.tipoUsuario").value("ADMIN"));
    }
}
