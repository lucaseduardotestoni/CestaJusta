package org.furb.controller;

import org.furb.dto.mercado.MercadoResponseDTO;
import org.furb.services.MercadoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MercadoControllerTest {

    private MockMvc mockMvc;
    private MercadoService service;

    @BeforeEach
    void setUp() {
        service = mock(MercadoService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new MercadoController(service)).build();
    }

    private MercadoResponseDTO dto() {
        return new MercadoResponseDTO(1L, "Super Koch", "11.111.111/0001-11", "Blumenau", "SC", true);
    }

    @Test
    void listarTodos_retorna200ComArray() throws Exception {
        when(service.listarTodos()).thenReturn(List.of(dto()));

        mockMvc.perform(get("/mercados/todos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].nomeFantasia").value("Super Koch"));
        verify(service).listarTodos();
    }

    @Test
    void editar_retorna200ComDto() throws Exception {
        when(service.editar(eq(1L), any())).thenReturn(dto());

        mockMvc.perform(put("/mercados/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nomeFantasia\":\"Super Koch\",\"cnpj\":\"11.111.111/0001-11\",\"cidade\":\"Blumenau\",\"estado\":\"SC\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cidade").value("Blumenau"));
    }
}
