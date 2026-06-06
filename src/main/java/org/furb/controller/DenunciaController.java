package org.furb.controller;

import jakarta.validation.Valid;
import org.furb.dto.denuncia.DenunciaCadastroDTO;
import org.furb.dto.denuncia.DenunciaResponseDTO;
import org.furb.dto.denuncia.VotoDenunciaDTO;
import org.furb.enums.StatusDenuncia;
import org.furb.services.DenunciaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/denuncias")
public class DenunciaController {

    private final DenunciaService denunciaService;

    public DenunciaController(DenunciaService denunciaService) {
        this.denunciaService = denunciaService;
    }

    @PostMapping
    public ResponseEntity<Void> criar(@Valid @RequestBody DenunciaCadastroDTO dto) {
        denunciaService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DenunciaResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(denunciaService.buscarPorId(id));
    }

    @GetMapping("/preco/{precoId}")
    public ResponseEntity<List<DenunciaResponseDTO>> listarPorPreco(@PathVariable Long precoId) {
        return ResponseEntity.ok(denunciaService.listarPorPreco(precoId));
    }

    @PostMapping("/{id}/votos")
    public ResponseEntity<Void> votar(@PathVariable Long id, @Valid @RequestBody VotoDenunciaDTO dto) {
        denunciaService.votar(id, dto.getTipo());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/votos")
    public ResponseEntity<Void> retirarVoto(@PathVariable Long id) {
        denunciaService.retirarVoto(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<Void> cancelar(@PathVariable Long id) {
        denunciaService.cancelar(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/resolver")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> resolver(@PathVariable Long id, @RequestParam StatusDenuncia status) {
        denunciaService.resolverComoAdmin(id, status);
        return ResponseEntity.noContent().build();
    }
}
