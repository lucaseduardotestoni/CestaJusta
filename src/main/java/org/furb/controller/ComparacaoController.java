package org.furb.controller;

import org.furb.dto.comparacao.ComparacaoPrecoDTO;
import org.furb.services.PrecoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/comparacoes")
public class ComparacaoController {

    private final PrecoService precoService;

    public ComparacaoController(PrecoService precoService) {
        this.precoService = precoService;
    }

    @GetMapping("/produto/{produtoId}")
    public ResponseEntity<ComparacaoPrecoDTO> compararPorProduto(@PathVariable Long produtoId) {
        return ResponseEntity.ok(precoService.compararPorProduto(produtoId));
    }
}
