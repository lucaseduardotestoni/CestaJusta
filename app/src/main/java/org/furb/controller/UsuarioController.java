package org.furb.controller;

import jakarta.validation.Valid;
import org.furb.dto.usuario.AlterarPapelDTO;
import org.furb.dto.usuario.UsuarioCadastroDTO;
import org.furb.dto.usuario.UsuarioMeDTO;
import org.furb.dto.usuario.UsuarioResponseDTO;
import org.furb.dto.usuario.UsuarioUpdateDTO;
import org.furb.model.Usuario;
import org.furb.security.UsuarioAutenticadoProvider;
import org.furb.services.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UsuarioResponseDTO>> listarTodos() {
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    @PatchMapping("/{id}/papel")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsuarioResponseDTO> alterarPapel(@PathVariable Long id,
                                                           @Valid @RequestBody AlterarPapelDTO dto) {
        return ResponseEntity.ok(usuarioService.alterarPapel(id, dto.getTipoUsuario()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsuarioResponseDTO> inativar(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.inativar(id));
    }

    @PatchMapping("/{id}/ativar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsuarioResponseDTO> ativar(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.ativar(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsuarioResponseDTO> atualizar(@PathVariable Long id,
                                                        @Valid @RequestBody UsuarioUpdateDTO dto) {
        return ResponseEntity.ok(usuarioService.atualizar(id, dto));
    }
}
