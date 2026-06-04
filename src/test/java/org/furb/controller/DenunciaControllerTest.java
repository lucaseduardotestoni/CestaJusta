package org.furb.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.furb.dto.denuncia.DenunciaCadastroDTO;
import org.furb.enums.TipoVoto;
import org.furb.model.Denuncia;
import org.furb.services.DenunciaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DenunciaControllerTest {

    private MockMvc mockMvc;
    private DenunciaService service;
    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        service = mock(DenunciaService.class);
        DenunciaController controller = new DenunciaController(service);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void criar_retorna201() throws Exception {
        DenunciaCadastroDTO dto = new DenunciaCadastroDTO();
        dto.setPrecoId(10L);
        dto.setMotivo("Abusivo");
        Denuncia criada = new Denuncia();
        when(service.criar(any())).thenReturn(criada);

        mockMvc.perform(post("/denuncias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    void votar_retorna204() throws Exception {
        mockMvc.perform(post("/denuncias/100/votos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tipo\":\"CONFIRMA\"}"))
                .andExpect(status().isNoContent());
        verify(service).votar(eq(100L), eq(TipoVoto.CONFIRMA));
    }

    @Test
    void cancelar_retorna204() throws Exception {
        mockMvc.perform(patch("/denuncias/100/cancelar"))
                .andExpect(status().isNoContent());
        verify(service).cancelar(100L);
    }
}
