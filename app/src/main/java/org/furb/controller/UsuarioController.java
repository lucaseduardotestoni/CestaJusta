package org.furb.controller;

import jakarta.validation.Valid;
import org.furb.dto.usuario.UsuarioCadastroDTO;
import org.furb.dto.usuario.UsuarioMeDTO;
import org.furb.dto.usuario.UsuarioResponseDTO;
import org.furb.model.Usuario;
import org.furb.security.UsuarioAutenticadoProvider;
import org.furb.services.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final UsuarioAutenticadoProvider usuarioAutenticadoProvider;

    public UsuarioController(UsuarioService usuarioService,
                             UsuarioAutenticadoProvider usuarioAutenticadoProvider) {
        this.usuarioService = usuarioService;
        this.usuarioAutenticadoProvider = usuarioAutenticadoProvider;
    }

    @PostMapping("/cadastro")
    public ResponseEntity<UsuarioResponseDTO> cadastrar(@Valid @RequestBody UsuarioCadastroDTO dto) {
        UsuarioResponseDTO response = usuarioService.cadastrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/me")
    public UsuarioMeDTO me() {
        Usuario u = usuarioAutenticadoProvider.getUsuarioAutenticado();
        return new UsuarioMeDTO(u.getNome(), u.getEmail(), u.getTipoUsuario());
    }
}
