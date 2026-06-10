package org.furb.controller;

import org.furb.dto.dashboard.KpiDashboardDTO;
import org.furb.dto.dashboard.ProdutoDashboardDTO;
import org.furb.services.DashboardService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/kpis")
    public ResponseEntity<KpiDashboardDTO> getKpis() {
        return ResponseEntity.ok(dashboardService.montarKpis());
    }

    @GetMapping("/produtos")
    public ResponseEntity<Page<ProdutoDashboardDTO>> getProdutos(
            @RequestParam(defaultValue = "todos") String ordem,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<ProdutoDashboardDTO> result =
                dashboardService.listarProdutosDashboard(ordem, PageRequest.of(page, size));
        return ResponseEntity.ok(result);
    }
}
