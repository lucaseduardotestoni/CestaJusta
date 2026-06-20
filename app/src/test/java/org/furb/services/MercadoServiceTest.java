package org.furb.services;

import org.furb.dto.mercado.MercadoCadastroDTO;
import org.furb.dto.mercado.MercadoResponseDTO;
import org.furb.model.Mercado;
import org.furb.repositories.MercadoRepository;
import org.furb.services.exeptions.BusinessException;
import org.furb.services.exeptions.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MercadoServiceTest {

    @Mock private MercadoRepository mercadoRepository;
    @InjectMocks private MercadoService mercadoService;

    private static Mercado mercado(Long id, String nome, String cnpj, boolean ativo) {
        Mercado m = new Mercado();
        m.setNomeFantasia(nome);
        m.setCnpj(cnpj);
        m.setAtivo(ativo);
        try {
            var f = Mercado.class.getDeclaredField("id");
            f.setAccessible(true);
            f.set(m, id);
        } catch (Exception e) { throw new RuntimeException(e); }
        return m;
    }

    private static MercadoCadastroDTO dto(String nome, String cnpj) {
        MercadoCadastroDTO d = new MercadoCadastroDTO();
        d.setNomeFantasia(nome);
        d.setCnpj(cnpj);
        d.setCidade("Blumenau");
        d.setEstado("SC");
        return d;
    }

    @Test
    void listarAtivos_retornaApenasAtivos() {
        when(mercadoRepository.findAll()).thenReturn(List.of(
                mercado(1L, "Ativo", "111", true),
                mercado(2L, "Inativo", "222", false)));

        List<MercadoResponseDTO> r = mercadoService.listarAtivos();

        assertThat(r).extracting(MercadoResponseDTO::getNomeFantasia).containsExactly("Ativo");
    }

    @Test
    void listarTodos_retornaAtivosEInativos() {
        when(mercadoRepository.findAll()).thenReturn(List.of(
                mercado(1L, "Ativo", "111", true),
                mercado(2L, "Inativo", "222", false)));

        List<MercadoResponseDTO> r = mercadoService.listarTodos();

        assertThat(r).extracting(MercadoResponseDTO::getNomeFantasia)
                .containsExactlyInAnyOrder("Ativo", "Inativo");
    }

    @Test
    void editar_atualizaCampos() {
        Mercado existente = mercado(10L, "Antigo", "111", true);
        when(mercadoRepository.findById(10L)).thenReturn(Optional.of(existente));
        when(mercadoRepository.findByCnpj("111")).thenReturn(Optional.of(existente));
        when(mercadoRepository.save(any(Mercado.class))).thenAnswer(i -> i.getArgument(0));

        MercadoResponseDTO r = mercadoService.editar(10L, dto("Novo Nome", "111"));

        assertThat(r.getNomeFantasia()).isEqualTo("Novo Nome");
        assertThat(r.getCidade()).isEqualTo("Blumenau");
        assertThat(r.getEstado()).isEqualTo("SC");
    }

    @Test
    void editar_cnpjDeOutroMercado_lancaBusinessException() {
        when(mercadoRepository.findById(10L)).thenReturn(Optional.of(mercado(10L, "A", "111", true)));
        when(mercadoRepository.findByCnpj("999")).thenReturn(Optional.of(mercado(20L, "Outro", "999", true)));

        assertThatThrownBy(() -> mercadoService.editar(10L, dto("A", "999")))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("CNPJ");
    }

    @Test
    void editar_idInexistente_lancaResourceNotFound() {
        when(mercadoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> mercadoService.editar(99L, dto("A", "111")))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
