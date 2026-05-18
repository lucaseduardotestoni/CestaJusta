package org.furb.controller;

import org.furb.dto.comparacao.ComparacaoPrecoDTO;
import org.furb.dto.dashboard.HistoricoPrecoDTO;
import org.furb.services.DashboardService;
import org.furb.services.PrecoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/comparacoes")
public class ComparacaoController {

    private final PrecoService precoService;
    private final DashboardService dashboardService;

    public ComparacaoController(PrecoService precoService, DashboardService dashboardService) {
        this.precoService = precoService;
        this.dashboardService = dashboardService;
    }

    @GetMapping("/produto/{produtoId}")
    public ResponseEntity<ComparacaoPrecoDTO> compararPorProduto(@PathVariable Long produtoId) {
        return ResponseEntity.ok(precoService.compararPorProduto(produtoId));
    }

    @GetMapping("/produto/{produtoId}/historico")
    public ResponseEntity<HistoricoPrecoDTO> historicoPorProduto(
            @PathVariable Long produtoId,
            @RequestParam(defaultValue = "30") int dias) {
        return ResponseEntity.ok(dashboardService.montarHistorico(produtoId, dias));
    }
}
