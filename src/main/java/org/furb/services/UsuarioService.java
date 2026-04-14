package org.furb.services;
import org.furb.dto.UsuarioCadastroDTO;
import org.furb.dto.UsuarioResponseDTO;
import org.furb.enums.TipoUsuario;
import org.furb.model.Usuario;
import org.furb.repositories.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UsuarioResponseDTO cadastrar(UsuarioCadastroDTO dto) {
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Já existe um usuário cadastrado com este e-mail.");
        }
        if (dto.getTipoUsuario() == TipoUsuario.ADMIN) {
            throw new RuntimeException("Não é permitido cadastrar usuário administrador.");
        }
        Usuario usuario = new Usuario();
        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        usuario.setSenha(passwordEncoder.encode(dto.getSenha()));
        usuario.setTipoUsuario(dto.getTipoUsuario());
        usuario.setAtivo(true);

        Usuario salvo = usuarioRepository.save(usuario);

        return new UsuarioResponseDTO(
                salvo.getId(),
                salvo.getNome(),
                salvo.getEmail(),
                salvo.getTipoUsuario(),
                salvo.getAtivo(),
                salvo.getDataCriacao()
        );
    }
}