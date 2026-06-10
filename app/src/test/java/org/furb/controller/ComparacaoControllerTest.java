package org.furb.controller;

import org.furb.dto.dashboard.HistoricoPrecoDTO;
import org.furb.dto.dashboard.PontoSparklineDTO;
import org.furb.services.DashboardService;
import org.furb.services.PrecoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ComparacaoControllerTest {

    private MockMvc mockMvc;
    private DashboardService dashboardService;
    private PrecoService precoService;

    @BeforeEach
    void setUp() {
        precoService = mock(PrecoService.class);
        dashboardService = mock(DashboardService.class);
        ComparacaoController controller = new ComparacaoController(precoService, dashboardService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void historicoPorProduto_retornaDtoComPontos() throws Exception {
        HistoricoPrecoDTO dto = new HistoricoPrecoDTO(1L, "Leite Italac 1L",
                List.of(
                        new PontoSparklineDTO(LocalDate.of(2026, 4, 30), new BigDecimal("4.50")),
                        new PontoSparklineDTO(LocalDate.of(2026, 5, 1),  new BigDecimal("4.80"))
                ));
        when(dashboardService.montarHistorico(eq(1L), eq(30))).thenReturn(dto);

        mockMvc.perform(get("/comparacoes/produto/1/historico"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.produtoId").value(1))
                .andExpect(jsonPath("$.produtoNome").value("Leite Italac 1L"))
                .andExpect(jsonPath("$.pontos").isArray())
                .andExpect(jsonPath("$.pontos.length()").value(2));
    }

    @Test
    void historicoPorProduto_aceitaQueryParamDias() throws Exception {
        HistoricoPrecoDTO dto = new HistoricoPrecoDTO(1L, "Leite", List.of());
        when(dashboardService.montarHistorico(eq(1L), eq(7))).thenReturn(dto);

        mockMvc.perform(get("/comparacoes/produto/1/historico").param("dias", "7"))
                .andExpect(status().isOk());

        verify(dashboardService).montarHistorico(eq(1L), eq(7));
    }
}
