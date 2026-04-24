package org.furb.controller;

import org.furb.dto.preco.PrecoCadastroDTO;
import org.furb.dto.preco.PrecoResponseDTO;
import org.furb.services.PrecoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/precos")
public class PrecoController {

    private final PrecoService precoService;

    public PrecoController(PrecoService precoService) {
        this.precoService = precoService;
    }

    @PostMapping("/cadastro")
    public ResponseEntity<PrecoResponseDTO> cadastrar(@Valid @RequestBody PrecoCadastroDTO dto) {
        PrecoResponseDTO response = precoService.cadastrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PrecoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(precoService.buscarPorId(id));
    }

    @GetMapping("/produto/{produtoId}")
    public ResponseEntity<List<PrecoResponseDTO>> listarPorProduto(@PathVariable Long produtoId) {
        return ResponseEntity.ok(precoService.listarPorProduto(produtoId));
    }

    @GetMapping("/mercado/{mercadoId}")
    public ResponseEntity<List<PrecoResponseDTO>> listarPorMercado(@PathVariable Long mercadoId) {
        return ResponseEntity.ok(precoService.listarPorMercado(mercadoId));
    }
}