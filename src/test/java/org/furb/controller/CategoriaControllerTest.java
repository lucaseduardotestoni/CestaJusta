package org.furb.controller;

import org.furb.dto.categoria.CategoriaResponseDTO;
import org.furb.services.CategoriaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CategoriaControllerTest {

    private MockMvc mockMvc;
    private CategoriaService service;

    @BeforeEach
    void setUp() {
        service = mock(CategoriaService.class);
        CategoriaController controller = new CategoriaController(service);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void listarTodas_retorna200ComLista() throws Exception {
        when(service.listarTodas()).thenReturn(List.of(new CategoriaResponseDTO(1L, "Grãos")));

        mockMvc.perform(get("/categorias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Grãos"));
    }
}