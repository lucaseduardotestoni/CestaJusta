package org.furb.controller;

import org.furb.dto.mercadoComerciante.MercadoComercianteResponseDTO;
import org.furb.services.MercadoComercianteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MercadoComercianteController {

    private final MercadoComercianteService service;

    public MercadoComercianteController(MercadoComercianteService service) {
        this.service = service;
    }

    @PostMapping("/mercados/{mercadoId}/comerciantes/{comercianteId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MercadoComercianteResponseDTO> associar(@PathVariable Long mercadoId,
                                                                   @PathVariable Long comercianteId) {
        MercadoComercianteResponseDTO response = service.associar(mercadoId, comercianteId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/mercados/{mercadoId}/comerciantes/{comercianteId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> desassociar(@PathVariable Long mercadoId,
                                             @PathVariable Long comercianteId) {
        service.desassociar(mercadoId, comercianteId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/mercados/{mercadoId}/comerciantes")
    public ResponseEntity<List<MercadoComercianteResponseDTO>> listarDonosDoMercado(@PathVariable Long mercadoId) {
        return ResponseEntity.ok(service.listarDonosDoMercado(mercadoId));
    }

    @GetMapping("/comerciantes/{comercianteId}/mercados")
    public ResponseEntity<List<MercadoComercianteResponseDTO>> listarMercadosDoComerciante(@PathVariable Long comercianteId) {
        return ResponseEntity.ok(service.listarMercadosDoComerciante(comercianteId));
    }
}