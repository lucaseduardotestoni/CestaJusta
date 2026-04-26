package org.furb.controller;

import org.furb.dto.mercado.MercadoCadastroDTO;
import org.furb.dto.mercado.MercadoResponseDTO;
import org.furb.services.MercadoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mercados")
public class MercadoController {

    private final MercadoService mercadoService;

    public MercadoController(MercadoService mercadoService) {
        this.mercadoService = mercadoService;
    }

    @PostMapping("/cadastro")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MercadoResponseDTO> cadastrar(@Valid @RequestBody MercadoCadastroDTO dto) {
        MercadoResponseDTO response = mercadoService.cadastrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<MercadoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(mercadoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MercadoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(mercadoService.buscarPorId(id));
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<MercadoResponseDTO>> buscarPorNome(@RequestParam String nome) {
        return ResponseEntity.ok(mercadoService.buscarPorNome(nome));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MercadoResponseDTO> inativar(@PathVariable Long id) {
        return ResponseEntity.ok(mercadoService.inativar(id));
    }

    @PatchMapping("/{id}/ativar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MercadoResponseDTO> ativar(@PathVariable Long id) {
        return ResponseEntity.ok(mercadoService.ativar(id));
    }
}