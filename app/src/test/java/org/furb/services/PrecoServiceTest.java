package org.furb.services;

import org.furb.dto.preco.PrecoCadastroDTO;
import org.furb.dto.preco.PrecoResponseDTO;
import org.furb.enums.StatusPreco;
import org.furb.enums.TipoUsuario;
import org.furb.model.Mercado;
import org.furb.model.Preco;
import org.furb.model.Produto;
import org.furb.model.Usuario;
import org.furb.repositories.MercadoComercianteRepository;
import org.furb.repositories.MercadoRepository;
import org.furb.repositories.PrecoRepository;
import org.furb.repositories.ProdutoRepository;
import org.furb.security.UsuarioAutenticadoProvider;
import org.furb.services.exeptions.BusinessException;
import org.furb.services.exeptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PrecoServiceTest {

    @Mock private PrecoRepository precoRepository;
    @Mock private ProdutoRepository produtoRepository;
    @Mock private MercadoRepository mercadoRepository;
    @Mock private MercadoComercianteRepository mercadoComercianteRepository;
    @Mock private UsuarioAutenticadoProvider usuarioAutenticadoProvider;

    @InjectMocks
    private PrecoService precoService;

    private PrecoCadastroDTO dto;
    private Produto produto;
    private Mercado mercado;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        dto = new PrecoCadastroDTO();
        dto.setProdutoId(1L);
        dto.setMercadoId(2L);
        dto.setValor(new BigDecimal("9.99"));
        dto.setDataColeta(LocalDate.now());

        produto = new Produto();
        produto.setNome("Arroz 5kg");
        produto.setAtivo(true);

        mercado = new Mercado();
        mercado.setNomeFantasia("Supermercado X");
        mercado.setAtivo(true);

        usuario = new Usuario();
        usuario.setNome("Lucas");
        usuario.setEmail("lucas@teste.com");
        usuario.setTipoUsuario(TipoUsuario.CONSUMIDOR);
    }

    @Test
    void cadastrar_consumidor_salvaComStatusPendente() {
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(mercadoRepository.findById(2L)).thenReturn(Optional.of(mercado));
        when(usuarioAutenticadoProvider.getUsuarioAutenticado()).thenReturn(usuario);
        when(precoRepository.save(any(Preco.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PrecoResponseDTO response = precoService.cadastrar(dto);

        assertThat(response.getStatus()).isEqualTo(StatusPreco.PENDENTE);
        assertThat(response.getValor()).isEqualByComparingTo("9.99");
    }

    @Test
    void cadastrar_comercianteVinculadoAoMercado_salvaComStatusConfirmado() {
        usuario.setTipoUsuario(TipoUsuario.COMERCIANTE);

        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(mercadoRepository.findById(2L)).thenReturn(Optional.of(mercado));
        when(usuarioAutenticadoProvider.getUsuarioAutenticado()).thenReturn(usuario);
        when(mercadoComercianteRepository.existsByMercadoIdAndComercianteId(any(), any())).thenReturn(true);
        when(precoRepository.save(any(Preco.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PrecoResponseDTO response = precoService.cadastrar(dto);

        assertThat(response.getStatus()).isEqualTo(StatusPreco.CONFIRMADO);
    }

    @Test
    void cadastrar_produtoInexistente_lancaResourceNotFound() {
        when(produtoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> precoService.cadastrar(dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Produto");

        verify(precoRepository, never()).save(any());
    }

    @Test
    void cadastrar_produtoInativo_lancaBusinessException() {
        produto.setAtivo(false);
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));

        assertThatThrownBy(() -> precoService.cadastrar(dto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("inativo");

        verify(precoRepository, never()).save(any());
    }

    @Test
    void cadastrar_mercadoInativo_lancaBusinessException() {
        mercado.setAtivo(false);
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(mercadoRepository.findById(2L)).thenReturn(Optional.of(mercado));

        assertThatThrownBy(() -> precoService.cadastrar(dto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("inativo");

        verify(precoRepository, never()).save(any());
    }

}
