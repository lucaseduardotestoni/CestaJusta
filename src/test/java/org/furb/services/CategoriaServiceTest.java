package org.furb.services;

import org.furb.dto.categoria.CategoriaResponseDTO;
import org.furb.model.Categoria;
import org.furb.repositories.CategoriaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceTest {

    @Mock private CategoriaRepository categoriaRepository;

    @InjectMocks
    private CategoriaService categoriaService;

    @Test
    void listarTodas_retornaCategoriasAtivas() {
        Categoria c = new Categoria();
        c.setNome("Grãos");
        when(categoriaRepository.findByAtivoTrue()).thenReturn(List.of(c));

        List<CategoriaResponseDTO> resultado = categoriaService.listarTodas();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNome()).isEqualTo("Grãos");
    }
}