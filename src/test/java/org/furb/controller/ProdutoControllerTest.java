package org.furb.controller;

import org.furb.dto.produto.ProdutoResponseDTO;
import org.furb.services.ProdutoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProdutoControllerTest {

    private MockMvc mockMvc;
    private ProdutoService produtoService;

    @BeforeEach
    void setUp() {
        produtoService = mock(ProdutoService.class);
        ProdutoController controller = new ProdutoController(produtoService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void buscarPorCategoria_retornaLista() throws Exception {
        ProdutoResponseDTO dto = new ProdutoResponseDTO(
                1L, "Arroz 5kg", "789000", "Tio João", "5kg", "Grãos", true);
        when(produtoService.buscarPorCategoria(eq(1L))).thenReturn(List.of(dto));

        mockMvc.perform(get("/produtos/categoria/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].nome").value("Arroz 5kg"));
    }
}
