package org.furb.services;

import org.furb.enums.StatusPreco;
import org.furb.model.Preco;
import org.furb.model.Usuario;
import org.furb.repositories.ConfirmacaoPrecoRepository;
import org.furb.repositories.PrecoRepository;
import org.furb.security.UsuarioAutenticadoProvider;
import org.furb.services.exeptions.BusinessException;
import org.furb.services.exeptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PrecoConfirmacaoServiceTest {

    @Mock private PrecoRepository precoRepository;
    @Mock private ConfirmacaoPrecoRepository confirmacaoPrecoRepository;
    @Mock private UsuarioAutenticadoProvider usuarioAutenticadoProvider;

    @InjectMocks
    private PrecoConfirmacaoService service;

    private Preco preco;
    private Usuario autor;
    private Usuario confirmador;

    @BeforeEach
    void setUp() {
        autor = new Usuario();
        autor.setNome("Autor");
        setId(autor, 1L);

        confirmador = new Usuario();
        confirmador.setNome("Confirmador");
        setId(confirmador, 2L);

        preco = new Preco();
        preco.setUsuario(autor);
        preco.setStatus(StatusPreco.PENDENTE);
        setId(preco, 10L);
    }

    private static void setId(Object entity, Long id) {
        try {
            var f = entity.getClass().getDeclaredField("id");
            f.setAccessible(true);
            f.set(entity, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void confirmar_precoInexistente_lancaResourceNotFound() {
        when(usuarioAutenticadoProvider.getUsuarioAutenticado()).thenReturn(confirmador);
        when(precoRepository.findById(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.confirmar(10L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void confirmar_autorNaoPodeConfirmar_lancaBusinessException() {
        when(usuarioAutenticadoProvider.getUsuarioAutenticado()).thenReturn(autor);
        when(precoRepository.findById(10L)).thenReturn(Optional.of(preco));

        assertThatThrownBy(() -> service.confirmar(10L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("próprio");

        verify(confirmacaoPrecoRepository, never()).save(any());
    }

    @Test
    void confirmar_precoNaoPendente_lancaBusinessException() {
        preco.setStatus(StatusPreco.CONFIRMADO);
        when(usuarioAutenticadoProvider.getUsuarioAutenticado()).thenReturn(confirmador);
        when(precoRepository.findById(10L)).thenReturn(Optional.of(preco));

        assertThatThrownBy(() -> service.confirmar(10L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("pendente");
    }

    @Test
    void confirmar_jaConfirmou_lancaBusinessException() {
        when(usuarioAutenticadoProvider.getUsuarioAutenticado()).thenReturn(confirmador);
        when(precoRepository.findById(10L)).thenReturn(Optional.of(preco));
        when(confirmacaoPrecoRepository.existsByPrecoIdAndUsuarioId(10L, 2L)).thenReturn(true);

        assertThatThrownBy(() -> service.confirmar(10L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("já confirmou");
    }

    @Test
    void confirmar_terceiraConfirmacao_marcaComoConfirmado() {
        when(usuarioAutenticadoProvider.getUsuarioAutenticado()).thenReturn(confirmador);
        when(precoRepository.findById(10L)).thenReturn(Optional.of(preco));
        when(confirmacaoPrecoRepository.existsByPrecoIdAndUsuarioId(10L, 2L)).thenReturn(false);
        when(confirmacaoPrecoRepository.countByPrecoId(10L)).thenReturn(3L);

        service.confirmar(10L);

        assertThat(preco.getStatus()).isEqualTo(StatusPreco.CONFIRMADO);
        verify(precoRepository).save(preco);
    }

    @Test
    void confirmar_segundaConfirmacao_continuaPendente() {
        when(usuarioAutenticadoProvider.getUsuarioAutenticado()).thenReturn(confirmador);
        when(precoRepository.findById(10L)).thenReturn(Optional.of(preco));
        when(confirmacaoPrecoRepository.existsByPrecoIdAndUsuarioId(10L, 2L)).thenReturn(false);
        when(confirmacaoPrecoRepository.countByPrecoId(10L)).thenReturn(2L);

        service.confirmar(10L);

        assertThat(preco.getStatus()).isEqualTo(StatusPreco.PENDENTE);
    }

    @Test
    void retirarConfirmacao_existente_remove() {
        when(usuarioAutenticadoProvider.getUsuarioAutenticado()).thenReturn(confirmador);
        when(precoRepository.findById(10L)).thenReturn(Optional.of(preco));
        when(confirmacaoPrecoRepository.existsByPrecoIdAndUsuarioId(10L, 2L)).thenReturn(true);

        service.retirarConfirmacao(10L);

        verify(confirmacaoPrecoRepository).deleteByPrecoIdAndUsuarioId(10L, 2L);
    }

    @Test
    void retirarConfirmacao_inexistente_lancaResourceNotFound() {
        when(usuarioAutenticadoProvider.getUsuarioAutenticado()).thenReturn(confirmador);
        when(precoRepository.findById(10L)).thenReturn(Optional.of(preco));
        when(confirmacaoPrecoRepository.existsByPrecoIdAndUsuarioId(10L, 2L)).thenReturn(false);

        assertThatThrownBy(() -> service.retirarConfirmacao(10L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(confirmacaoPrecoRepository, never()).deleteByPrecoIdAndUsuarioId(any(), any());
    }
}
