package org.furb.services;

import org.furb.dto.produto.ProdutoCadastroDTO;
import org.furb.dto.produto.ProdutoResponseDTO;
import org.furb.enums.FotoStatus;
import org.furb.messaging.contract.RoutingKeys;
import org.furb.model.Categoria;
import org.furb.model.Produto;
import org.furb.outbox.OutboxService;
import org.furb.repositories.CategoriaRepository;
import org.furb.repositories.ProdutoRepository;
import org.furb.services.exeptions.BusinessException;
import org.furb.services.exeptions.ResourceNotFoundException;
import org.furb.storage.FotoStorage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

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
class ProdutoServiceTest {

    @Mock private ProdutoRepository produtoRepository;
    @Mock private CategoriaRepository categoriaRepository;
    @Mock private FotoStorage fotoStorage;
    @Mock private OutboxService outboxService;

    @InjectMocks
    private ProdutoService produtoService;

    private static Categoria categoriaGraos() {
        Categoria c = new Categoria();
        c.setNome("Grãos");
        return c;
    }

    @Test
    void buscarPorCategoria_categoriaInexistente_lancaResourceNotFound() {
        when(categoriaRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> produtoService.buscarPorCategoria(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Categoria");
    }

    @Test
    void buscarPorCategoria_retornaProdutosDaCategoria() {
        Categoria categoria = new Categoria();
        categoria.setNome("Grãos");

        Produto produto = new Produto();
        produto.setNome("Arroz 5kg");
        produto.setCategoria(categoria);
        produto.setAtivo(true);

        when(categoriaRepository.existsById(1L)).thenReturn(true);
        when(produtoRepository.findByCategoriaIdAndAtivoTrue(1L)).thenReturn(List.of(produto));

        List<ProdutoResponseDTO> resultado = produtoService.buscarPorCategoria(1L);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNome()).isEqualTo("Arroz 5kg");
    }

    @Test
    void listarTodos_retornaAtivosEInativos() {
        Categoria categoria = new Categoria();
        categoria.setNome("Grãos");

        Produto ativo = new Produto();
        ativo.setNome("Arroz 5kg");
        ativo.setCategoria(categoria);
        ativo.setAtivo(true);

        Produto inativo = new Produto();
        inativo.setNome("Feijão antigo");
        inativo.setCategoria(categoria);
        inativo.setAtivo(false);

        when(produtoRepository.findAll()).thenReturn(java.util.List.of(ativo, inativo));

        List<ProdutoResponseDTO> resultado = produtoService.listarTodos();

        assertThat(resultado).hasSize(2);
        assertThat(resultado).extracting(ProdutoResponseDTO::getAtivo).contains(true, false);
    }

    @Test
    void listarAtivos_naoRetornaInativos() {
        Categoria categoria = new Categoria();
        categoria.setNome("Grãos");

        Produto ativo = new Produto();
        ativo.setNome("Arroz 5kg");
        ativo.setCategoria(categoria);
        ativo.setAtivo(true);

        Produto inativo = new Produto();
        inativo.setNome("Feijão antigo");
        inativo.setCategoria(categoria);
        inativo.setAtivo(false);

        when(produtoRepository.findAll()).thenReturn(java.util.List.of(ativo, inativo));

        List<ProdutoResponseDTO> resultado = produtoService.listarAtivos();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNome()).isEqualTo("Arroz 5kg");
    }

    @Test
    void cadastrar_comFoto_setaProcessandoEEmiteEvento() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoriaGraos()));
        when(produtoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(fotoStorage.store(eq("produtos"), any(), eq("png"))).thenReturn("produtos/abc.png");
        when(outboxService.novoEventoId()).thenReturn("evt-1");

        ProdutoCadastroDTO dto = new ProdutoCadastroDTO();
        dto.setNome("Arroz");
        dto.setCategoriaId(1L);
        MultipartFile foto = new MockMultipartFile("foto", "a.png", "image/png", new byte[]{1, 2, 3});

        ProdutoResponseDTO resp = produtoService.cadastrar(dto, foto);

        assertThat(resp.getImagemPath()).isEqualTo("produtos/abc.png");
        verify(outboxService).registrar(eq("evt-1"), eq(RoutingKeys.FOTO_SOLICITADA), any());
    }

    @Test
    void cadastrar_semFoto_naoEmiteEvento() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoriaGraos()));
        when(produtoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ProdutoCadastroDTO dto = new ProdutoCadastroDTO();
        dto.setNome("Feijão");
        dto.setCategoriaId(1L);

        produtoService.cadastrar(dto, null);

        verify(outboxService, never()).registrar(any(), any(), any());
    }

    @Test
    void cadastrar_fotoTipoInvalido_lancaBusinessException() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoriaGraos()));

        ProdutoCadastroDTO dto = new ProdutoCadastroDTO();
        dto.setNome("X");
        dto.setCategoriaId(1L);
        MultipartFile foto = new MockMultipartFile("foto", "a.gif", "image/gif", new byte[]{1});

        assertThatThrownBy(() -> produtoService.cadastrar(dto, foto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("imagem");
    }

    @Test
    void cadastrar_fotoAcimaDoLimite_lancaBusinessException() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoriaGraos()));

        ProdutoCadastroDTO dto = new ProdutoCadastroDTO();
        dto.setNome("X");
        dto.setCategoriaId(1L);
        MultipartFile foto = new MockMultipartFile("foto", "a.jpg", "image/jpeg", new byte[6 * 1024 * 1024]);

        assertThatThrownBy(() -> produtoService.cadastrar(dto, foto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("5MB");
    }

    @Test
    void editar_atualizaCamposEMantemImagemSemFotoNova() {
        Produto existente = new Produto();
        existente.setNome("Velho");
        existente.setImagemPath("produtos/old.jpg");
        existente.setAtivo(true);
        when(produtoRepository.findById(5L)).thenReturn(Optional.of(existente));
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoriaGraos()));
        when(produtoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ProdutoCadastroDTO dto = new ProdutoCadastroDTO();
        dto.setNome("Novo");
        dto.setCategoriaId(1L);

        ProdutoResponseDTO resp = produtoService.editar(5L, dto, null);

        assertThat(resp.getNome()).isEqualTo("Novo");
        assertThat(resp.getImagemPath()).isEqualTo("produtos/old.jpg");
        verify(outboxService, never()).registrar(any(), any(), any());
    }
}
