package org.furb.controller;

import org.furb.services.PrecoConfirmacaoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PrecoConfirmacaoControllerTest {

    private MockMvc mockMvc;
    private PrecoConfirmacaoService service;

    @BeforeEach
    void setUp() {
        service = mock(PrecoConfirmacaoService.class);
        PrecoConfirmacaoController controller = new PrecoConfirmacaoController(service);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void confirmar_retorna204() throws Exception {
        mockMvc.perform(post("/precos/10/confirmacoes"))
                .andExpect(status().isNoContent());
        verify(service).confirmar(10L);
    }

    @Test
    void retirar_retorna204() throws Exception {
        mockMvc.perform(delete("/precos/10/confirmacoes"))
                .andExpect(status().isNoContent());
        verify(service).retirarConfirmacao(10L);
    }
}
