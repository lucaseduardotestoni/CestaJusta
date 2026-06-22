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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UsuarioAutenticadoProvider usuarioAutenticadoProvider;

    @InjectMocks
    private UsuarioService usuarioService;

    private UsuarioCadastroDTO dto;

    @BeforeEach
    void setUp() {
        dto = new UsuarioCadastroDTO();
        dto.setNome("Lucas");
        dto.setEmail("lucas@teste.com");
        dto.setSenha("senha123");
        dto.setTipoUsuario(TipoUsuario.CONSUMIDOR);
    }

    private Usuario usuario(Long id, TipoUsuario tipo, boolean ativo) {
        Usuario u = new Usuario();
        u.setNome("Fulano");
        u.setEmail("fulano@teste.com");
        u.setSenha("$2a$hash");
        u.setTipoUsuario(tipo);
        u.setAtivo(ativo);
        u.setDataCriacao(LocalDateTime.now());
        setId(u, id);
        return u;
    }

    // Usuario.id não tem setter; reflexão só nos testes para simular entidade persistida.
    private void setId(Usuario u, Long id) {
        try {
            var f = Usuario.class.getDeclaredField("id");
            f.setAccessible(true);
            f.set(u, id);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }

    @Test
    void cadastrar_dadosValidos_retornaUsuarioResponseDTO() {
        when(usuarioRepository.existsByEmail("lucas@teste.com")).thenReturn(false);
        when(passwordEncoder.encode("senha123")).thenReturn("$2a$hash");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario u = invocation.getArgument(0);
            u.setDataCriacao(LocalDateTime.now());
            return u;
        });

        UsuarioResponseDTO response = usuarioService.cadastrar(dto);

        assertThat(response.getNome()).isEqualTo("Lucas");
        assertThat(response.getEmail()).isEqualTo("lucas@teste.com");
        assertThat(response.getTipoUsuario()).isEqualTo(TipoUsuario.CONSUMIDOR);
        assertThat(response.getAtivo()).isTrue();
        verify(passwordEncoder).encode("senha123");
    }

    @Test
    void cadastrar_emailJaExistente_lancaBusinessException() {
        when(usuarioRepository.existsByEmail("lucas@teste.com")).thenReturn(true);

        assertThatThrownBy(() -> usuarioService.cadastrar(dto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("e-mail");

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void cadastrar_tipoAdmin_lancaBusinessException() {
        dto.setTipoUsuario(TipoUsuario.ADMIN);
        when(usuarioRepository.existsByEmail("lucas@teste.com")).thenReturn(false);

        assertThatThrownBy(() -> usuarioService.cadastrar(dto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("administrador");

        verify(passwordEncoder, never()).encode(eq("senha123"));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void listarTodos_retornaTodosOsUsuarios() {
        when(usuarioRepository.findAll()).thenReturn(List.of(
                usuario(1L, TipoUsuario.ADMIN, true),
                usuario(2L, TipoUsuario.CONSUMIDOR, false)
        ));

        List<UsuarioResponseDTO> lista = usuarioService.listarTodos();

        assertThat(lista).hasSize(2);
        assertThat(lista).extracting(UsuarioResponseDTO::getTipoUsuario)
                .containsExactly(TipoUsuario.ADMIN, TipoUsuario.CONSUMIDOR);
    }

    @Test
    void alterarPapel_usuarioDiferente_atualizaTipo() {
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(usuario(2L, TipoUsuario.CONSUMIDOR, true)));
        when(usuarioAutenticadoProvider.getUsuarioAutenticado()).thenReturn(usuario(1L, TipoUsuario.ADMIN, true));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

        UsuarioResponseDTO response = usuarioService.alterarPapel(2L, TipoUsuario.COMERCIANTE);

        assertThat(response.getTipoUsuario()).isEqualTo(TipoUsuario.COMERCIANTE);
    }

    @Test
    void alterarPapel_usuarioInexistente_lancaResourceNotFound() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioService.alterarPapel(99L, TipoUsuario.COMERCIANTE))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void alterarPapel_rebaixaProprioAdmin_lancaBusinessException() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario(1L, TipoUsuario.ADMIN, true)));
        when(usuarioAutenticadoProvider.getUsuarioAutenticado()).thenReturn(usuario(1L, TipoUsuario.ADMIN, true));

        assertThatThrownBy(() -> usuarioService.alterarPapel(1L, TipoUsuario.CONSUMIDOR))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("próprio");

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void inativar_outroUsuarioAtivo_desativa() {
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(usuario(2L, TipoUsuario.CONSUMIDOR, true)));
        when(usuarioAutenticadoProvider.getUsuarioAutenticado()).thenReturn(usuario(1L, TipoUsuario.ADMIN, true));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

        UsuarioResponseDTO response = usuarioService.inativar(2L);

        assertThat(response.getAtivo()).isFalse();
    }

    @Test
    void inativar_propriaConta_lancaBusinessException() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario(1L, TipoUsuario.ADMIN, true)));
        when(usuarioAutenticadoProvider.getUsuarioAutenticado()).thenReturn(usuario(1L, TipoUsuario.ADMIN, true));

        assertThatThrownBy(() -> usuarioService.inativar(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("própria");

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void inativar_jaInativo_lancaBusinessException() {
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(usuario(2L, TipoUsuario.CONSUMIDOR, false)));
        when(usuarioAutenticadoProvider.getUsuarioAutenticado()).thenReturn(usuario(1L, TipoUsuario.ADMIN, true));

        assertThatThrownBy(() -> usuarioService.inativar(2L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("inativo");

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void ativar_usuarioInativo_reativa() {
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(usuario(2L, TipoUsuario.CONSUMIDOR, false)));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

        UsuarioResponseDTO response = usuarioService.ativar(2L);

        assertThat(response.getAtivo()).isTrue();
    }

    @Test
    void ativar_jaAtivo_lancaBusinessException() {
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(usuario(2L, TipoUsuario.CONSUMIDOR, true)));

        assertThatThrownBy(() -> usuarioService.ativar(2L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("ativo");

        verify(usuarioRepository, never()).save(any());
    }

    // --- atualizar ---

    private UsuarioUpdateDTO updateDto(String nome, String email, TipoUsuario tipo) {
        UsuarioUpdateDTO d = new UsuarioUpdateDTO();
        d.setNome(nome);
        d.setEmail(email);
        d.setTipoUsuario(tipo);
        return d;
    }

    @Test
    void atualizar_dadosValidos_retornaResponseDTO() {
        Usuario existente = usuario(2L, TipoUsuario.CONSUMIDOR, true);
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(existente));
        when(usuarioRepository.findByEmail("novo@teste.com")).thenReturn(Optional.empty());
        when(usuarioAutenticadoProvider.getUsuarioAutenticado()).thenReturn(usuario(1L, TipoUsuario.ADMIN, true));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

        UsuarioUpdateDTO dto = updateDto("Novo Nome", "novo@teste.com", TipoUsuario.COMERCIANTE);
        UsuarioResponseDTO response = usuarioService.atualizar(2L, dto);

        assertThat(response.getNome()).isEqualTo("Novo Nome");
        assertThat(response.getEmail()).isEqualTo("novo@teste.com");
        assertThat(response.getTipoUsuario()).isEqualTo(TipoUsuario.COMERCIANTE);
    }

    @Test
    void atualizar_mesmoEmail_naoLancaConflito() {
        Usuario existente = usuario(2L, TipoUsuario.CONSUMIDOR, true);
        existente.setEmail("fulano@teste.com");
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(existente));
        // E-mail inalterado: o serviço pula a checagem de unicidade, então não há stub de findByEmail.
        when(usuarioAutenticadoProvider.getUsuarioAutenticado()).thenReturn(usuario(1L, TipoUsuario.ADMIN, true));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

        UsuarioUpdateDTO dto = updateDto("Novo Nome", "fulano@teste.com", TipoUsuario.CONSUMIDOR);
        UsuarioResponseDTO response = usuarioService.atualizar(2L, dto);

        assertThat(response.getNome()).isEqualTo("Novo Nome");
    }

    @Test
    void atualizar_usuarioInexistente_lancaResourceNotFound() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioService.atualizar(99L, updateDto("Nome", "x@x.com", TipoUsuario.CONSUMIDOR)))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void atualizar_emailEmUsoOutroUsuario_lancaBusinessException() {
        Usuario existente = usuario(2L, TipoUsuario.CONSUMIDOR, true);
        Usuario outro = usuario(3L, TipoUsuario.CONSUMIDOR, true);
        outro.setEmail("ocupado@teste.com");
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(existente));
        when(usuarioRepository.findByEmail("ocupado@teste.com")).thenReturn(Optional.of(outro));

        assertThatThrownBy(() -> usuarioService.atualizar(2L, updateDto("Nome", "ocupado@teste.com", TipoUsuario.CONSUMIDOR)))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("e-mail");

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void atualizar_rebaixaProprioAdmin_lancaBusinessException() {
        Usuario eu = usuario(1L, TipoUsuario.ADMIN, true);
        eu.setEmail("admin@teste.com");
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(eu));
        when(usuarioAutenticadoProvider.getUsuarioAutenticado()).thenReturn(usuario(1L, TipoUsuario.ADMIN, true));

        // E-mail inalterado: pula a checagem de unicidade e chega na trava anti-lockout.
        assertThatThrownBy(() -> usuarioService.atualizar(1L, updateDto("Admin", "admin@teste.com", TipoUsuario.CONSUMIDOR)))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("rebaixar");

        verify(usuarioRepository, never()).save(any());
    }
}
