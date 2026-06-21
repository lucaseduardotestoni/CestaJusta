package org.furb.controller;

import org.furb.dto.usuario.UsuarioResponseDTO;
import org.furb.enums.TipoUsuario;
import org.furb.security.UsuarioAutenticadoProvider;
import org.furb.services.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UsuarioControllerAdminTest {

    private MockMvc mockMvc;
    private UsuarioService service;

    @BeforeEach
    void setUp() {
        service = mock(UsuarioService.class);
        UsuarioAutenticadoProvider provider = mock(UsuarioAutenticadoProvider.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new UsuarioController(service, provider)).build();
    }

    private UsuarioResponseDTO dto(Long id, TipoUsuario tipo, boolean ativo) {
        return new UsuarioResponseDTO(id, "Fulano", "fulano@teste.com", tipo, ativo, LocalDateTime.now());
    }

    @Test
    void listarTodos_retorna200ComArray() throws Exception {
        when(service.listarTodos()).thenReturn(List.of(dto(1L, TipoUsuario.ADMIN, true)));

        mockMvc.perform(get("/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].tipoUsuario").value("ADMIN"));
        verify(service).listarTodos();
    }

    @Test
    void alterarPapel_retorna200ComDto() throws Exception {
        when(service.alterarPapel(eq(2L), eq(TipoUsuario.COMERCIANTE)))
                .thenReturn(dto(2L, TipoUsuario.COMERCIANTE, true));

        mockMvc.perform(patch("/usuarios/2/papel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tipoUsuario\":\"COMERCIANTE\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipoUsuario").value("COMERCIANTE"));
    }

    @Test
    void inativar_retorna200ComDto() throws Exception {
        when(service.inativar(eq(2L))).thenReturn(dto(2L, TipoUsuario.CONSUMIDOR, false));

        mockMvc.perform(delete("/usuarios/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ativo").value(false));
        verify(service).inativar(2L);
    }

    @Test
    void ativar_retorna200ComDto() throws Exception {
        when(service.ativar(eq(2L))).thenReturn(dto(2L, TipoUsuario.CONSUMIDOR, true));

        mockMvc.perform(patch("/usuarios/2/ativar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ativo").value(true));
        verify(service).ativar(2L);
    }
}