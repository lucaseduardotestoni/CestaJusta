package org.furb.controller;

import org.furb.services.PrecoConfirmacaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/precos/{precoId}/confirmacoes")
public class PrecoConfirmacaoController {

    private final PrecoConfirmacaoService service;

    public PrecoConfirmacaoController(PrecoConfirmacaoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Void> confirmar(@PathVariable Long precoId) {
        service.confirmar(precoId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> retirar(@PathVariable Long precoId) {
        service.retirarConfirmacao(precoId);
        return ResponseEntity.noContent().build();
    }
}