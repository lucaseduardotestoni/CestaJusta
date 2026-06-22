package org.furb.services;
import org.furb.dto.usuario.UsuarioCadastroDTO;
import org.furb.dto.usuario.UsuarioResponseDTO;
import org.furb.dto.usuario.UsuarioUpdateDTO;
import org.furb.enums.TipoUsuario;
import org.furb.model.Usuario;
import org.furb.repositories.UsuarioRepository;
import org.furb.security.UsuarioAutenticadoProvider;
import org.furb.services.exeptions.BusinessException;
import org.furb.services.exeptions.ResourceNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioAutenticadoProvider usuarioAutenticadoProvider;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          PasswordEncoder passwordEncoder,
                          UsuarioAutenticadoProvider usuarioAutenticadoProvider) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.usuarioAutenticadoProvider = usuarioAutenticadoProvider;
    }

    public UsuarioResponseDTO cadastrar(UsuarioCadastroDTO dto) {
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new BusinessException("Já existe um usuário cadastrado com este e-mail.");
        }
        if (dto.getTipoUsuario() == TipoUsuario.ADMIN) {
            throw new BusinessException("Não é permitido cadastrar usuário administrador.");
        }
        Usuario usuario = new Usuario();
        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        usuario.setSenha(passwordEncoder.encode(dto.getSenha()));
        usuario.setTipoUsuario(dto.getTipoUsuario());
        usuario.setAtivo(true);

        return toResponseDTO(usuarioRepository.save(usuario));
    }

    public List<UsuarioResponseDTO> listarTodos() {
        return usuarioRepository.findAll().stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public UsuarioResponseDTO alterarPapel(Long id, TipoUsuario novoTipo) {
        if (novoTipo == null) {
            throw new BusinessException("Tipo de usuário é obrigatório.");
        }
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));

        Usuario autenticado = usuarioAutenticadoProvider.getUsuarioAutenticado();
        if (autenticado.getId().equals(id) && novoTipo != TipoUsuario.ADMIN) {
            throw new BusinessException("Você não pode rebaixar o seu próprio papel de administrador.");
        }

        usuario.setTipoUsuario(novoTipo);
        return toResponseDTO(usuarioRepository.save(usuario));
    }

    public UsuarioResponseDTO inativar(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));

        Usuario autenticado = usuarioAutenticadoProvider.getUsuarioAutenticado();
        if (autenticado.getId().equals(id)) {
            throw new BusinessException("Você não pode inativar a sua própria conta.");
        }
        if (!usuario.getAtivo()) {
            throw new BusinessException("Usuário já está inativo.");
        }

        usuario.setAtivo(false);
        return toResponseDTO(usuarioRepository.save(usuario));
    }

    public UsuarioResponseDTO ativar(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));

        if (usuario.getAtivo()) {
            throw new BusinessException("Usuário já está ativo.");
        }

        usuario.setAtivo(true);
        return toResponseDTO(usuarioRepository.save(usuario));
    }

    public UsuarioResponseDTO atualizar(Long id, UsuarioUpdateDTO dto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));

        if (!usuario.getEmail().equals(dto.getEmail())) {
            usuarioRepository.findByEmail(dto.getEmail()).ifPresent(outro -> {
                throw new BusinessException("Já existe um usuário cadastrado com este e-mail.");
            });
        }

        Usuario autenticado = usuarioAutenticadoProvider.getUsuarioAutenticado();
        if (autenticado.getId().equals(id) && dto.getTipoUsuario() != TipoUsuario.ADMIN) {
            throw new BusinessException("Você não pode rebaixar o seu próprio papel de administrador.");
        }

        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        usuario.setTipoUsuario(dto.getTipoUsuario());
        return toResponseDTO(usuarioRepository.save(usuario));
    }

    private UsuarioResponseDTO toResponseDTO(Usuario usuario) {
        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getTipoUsuario(),
                usuario.getAtivo(),
                usuario.getDataCriacao()
        );
    }
}
