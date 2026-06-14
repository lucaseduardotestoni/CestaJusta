package org.furb.services;

import org.furb.dto.produto.ProdutoResponseDTO;
import org.furb.model.Categoria;
import org.furb.model.Produto;
import org.furb.repositories.CategoriaRepository;
import org.furb.repositories.ProdutoRepository;
import org.furb.services.exeptions.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProdutoServiceTest {

    @Mock private ProdutoRepository produtoRepository;
    @Mock private CategoriaRepository categoriaRepository;

    @InjectMocks
    private ProdutoService produtoService;

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
}
