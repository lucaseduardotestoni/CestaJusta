package org.furb.controller;

import org.furb.dto.dashboard.KpiDashboardDTO;
import org.furb.dto.dashboard.ProdutoDashboardDTO;
import org.furb.services.DashboardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = DashboardController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class}
)
@AutoConfigureMockMvc(addFilters = false)
class DashboardControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private DashboardService dashboardService;

    @Test
    void getKpis_retornaCorpoCompleto() throws Exception {
        KpiDashboardDTO dto = new KpiDashboardDTO(
                new BigDecimal("32.18"),
                new BigDecimal("-7.5"),
                42, 7,
                new BigDecimal("27.0"));
        when(dashboardService.montarKpis()).thenReturn(dto);

        mockMvc.perform(get("/dashboard/kpis"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valorCesta").value(32.18))
                .andExpect(jsonPath("$.variacaoSemanal").value(-7.5))
                .andExpect(jsonPath("$.totalProdutos").value(42))
                .andExpect(jsonPath("$.totalMercados").value(7))
                .andExpect(jsonPath("$.economiaMedia").value(27.0));
    }

    @Test
    void getProdutos_paginacao_respeitaQueryParams() throws Exception {
        Page<ProdutoDashboardDTO> empty = new PageImpl<>(List.of(), PageRequest.of(0, 5), 0);
        when(dashboardService.listarProdutosDashboard(eq("quedas"), any(Pageable.class)))
                .thenReturn(empty);

        mockMvc.perform(get("/dashboard/produtos")
                        .param("ordem", "quedas")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.size").value(5));

        verify(dashboardService).listarProdutosDashboard(eq("quedas"), any(Pageable.class));
    }

    @Test
    void getProdutos_semParams_usaDefaults() throws Exception {
        Page<ProdutoDashboardDTO> empty = new PageImpl<>(List.of(), PageRequest.of(0, 20), 0);
        when(dashboardService.listarProdutosDashboard(eq("todos"), any(Pageable.class)))
                .thenReturn(empty);

        mockMvc.perform(get("/dashboard/produtos"))
                .andExpect(status().isOk());

        verify(dashboardService).listarProdutosDashboard(eq("todos"), any(Pageable.class));
    }
}
