package org.furb.controller;

import org.furb.dto.denuncia.DenunciaListItemDTO;
import org.furb.dto.denuncia.DenunciaResponseDTO;
import org.furb.enums.StatusDenuncia;
import org.furb.enums.TipoVoto;
import org.furb.model.Denuncia;
import org.furb.services.DenunciaService;
import java.math.BigDecimal;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DenunciaControllerTest {

    private MockMvc mockMvc;
    private DenunciaService service;

    @BeforeEach
    void setUp() {
        service = mock(DenunciaService.class);
        DenunciaController controller = new DenunciaController(service);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void criar_retorna201() throws Exception {
        Denuncia criada = new Denuncia();
        when(service.criar(any(), any())).thenReturn(criada);

        mockMvc.perform(multipart("/denuncias")
                        .param("precoId", "10")
                        .param("motivo", "Abusivo"))
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

    @Test
    void buscarPorId_retorna200() throws Exception {
        DenunciaResponseDTO dto = new DenunciaResponseDTO(
                5L, 10L, 1L, "abusivo", null, StatusDenuncia.PENDENTE,
                2L, 1L, null, null, null, null, null, null);
        when(service.buscarPorId(5L)).thenReturn(dto);

        mockMvc.perform(get("/denuncias/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDENTE"));
    }

    @Test
    void listarPorPreco_retorna200() throws Exception {
        DenunciaResponseDTO dto = new DenunciaResponseDTO(
                100L, 10L, 1L, "preco errado", null, StatusDenuncia.PENDENTE,
                0L, 0L, null, null, null, null, null, null);
        when(service.listarPorPreco(10L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/denuncias/preco/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void retirarVoto_retorna204() throws Exception {
        mockMvc.perform(delete("/denuncias/100/votos"))
                .andExpect(status().isNoContent());
        verify(service).retirarVoto(100L);
    }

    @Test
    void resolver_retorna204() throws Exception {
        mockMvc.perform(put("/denuncias/100/resolver")
                        .param("status", "APROVADA"))
                .andExpect(status().isNoContent());
        verify(service).resolverComoAdmin(100L, StatusDenuncia.APROVADA);
    }

    private DenunciaListItemDTO listItem(Long id) {
        return new DenunciaListItemDTO(id, 10L, 1L, "Arroz Tio João 5kg", "Super Koch",
                new BigDecimal("18.90"), "abusivo", null, StatusDenuncia.PENDENTE,
                2L, 1L, null, null, null, null, null, null, null, true, null);
    }

    @Test
    void listarMinhas_retorna200ComArray() throws Exception {
        when(service.listarMinhas()).thenReturn(List.of(listItem(50L)));

        mockMvc.perform(get("/denuncias/minhas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].produtoNome").value("Arroz Tio João 5kg"))
                .andExpect(jsonPath("$[0].podeVotar").value(true));
    }

    @Test
    void listarTodas_semStatus_chamaServiceComNull() throws Exception {
        when(service.listarTodas(null)).thenReturn(List.of(listItem(50L)));

        mockMvc.perform(get("/denuncias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
        verify(service).listarTodas(null);
    }

    @Test
    void listarTodas_comStatus_repassaFiltro() throws Exception {
        when(service.listarTodas(StatusDenuncia.APROVADA)).thenReturn(List.of());

        mockMvc.perform(get("/denuncias").param("status", "APROVADA"))
                .andExpect(status().isOk());
        verify(service).listarTodas(StatusDenuncia.APROVADA);
    }
}
