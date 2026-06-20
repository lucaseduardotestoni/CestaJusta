package org.furb.controller;

import jakarta.validation.Valid;
import org.furb.dto.denuncia.DenunciaCadastroDTO;
import org.furb.dto.denuncia.DenunciaListItemDTO;
import org.furb.dto.denuncia.DenunciaResponseDTO;
import org.furb.dto.denuncia.VotoDenunciaDTO;
import org.furb.enums.StatusDenuncia;
import org.furb.services.DenunciaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/denuncias")
public class DenunciaController {

    private final DenunciaService denunciaService;

    public DenunciaController(DenunciaService denunciaService) {
        this.denunciaService = denunciaService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> criar(@Valid @ModelAttribute DenunciaCadastroDTO dto,
                                      @RequestPart(name = "foto", required = false) MultipartFile foto) {
        denunciaService.criar(dto, foto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/minhas")
    public ResponseEntity<List<DenunciaListItemDTO>> listarMinhas() {
        return ResponseEntity.ok(denunciaService.listarMinhas());
    }

    @GetMapping
    public ResponseEntity<List<DenunciaListItemDTO>> listarTodas(
            @RequestParam(required = false) StatusDenuncia status) {
        return ResponseEntity.ok(denunciaService.listarTodas(status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DenunciaResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(denunciaService.buscarPorId(id));
    }

    @GetMapping("/preco/{precoId}")
    public ResponseEntity<List<DenunciaResponseDTO>> listarPorPreco(@PathVariable Long precoId) {
        return ResponseEntity.ok(denunciaService.listarPorPreco(precoId));
    }

    @GetMapping("/meus-precos")
    public ResponseEntity<List<Long>> meusPrecosDenunciados() {
        return ResponseEntity.ok(denunciaService.precosDenunciadosPeloUsuarioNaJanela());
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
