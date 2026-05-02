package org.furb.services;

import org.furb.enums.StatusPreco;
import org.furb.model.Mercado;
import org.furb.model.Preco;
import org.furb.model.Produto;
import org.furb.repositories.MercadoRepository;
import org.furb.repositories.PrecoRepository;
import org.furb.repositories.ProdutoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class
DashboardServiceTest {

    @Mock private PrecoRepository precoRepository;
    @Mock private ProdutoRepository produtoRepository;
    @Mock private MercadoRepository mercadoRepository;

    @InjectMocks
    private DashboardService dashboardService;

    @Test
    void calcularValorCesta_quandoNenhumProdutoTemPreco_retornaZero() {
        when(precoRepository.findByDataColetaBetween(any(), any())).thenReturn(List.of());

        BigDecimal valor = dashboardService.calcularValorCesta();

        assertThat(valor).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void calcularValorCesta_somaMenorPrecoDeCadaProduto() {
        Produto leite = produtoMock(1L, "Leite");
        Produto arroz = produtoMock(2L, "Arroz");
        Mercado m1 = mercadoMock(1L, "Mercado A");
        Mercado m2 = mercadoMock(2L, "Mercado B");

        Preco leiteM1 = precoMock(leite, m1, "5.00", LocalDate.now().minusDays(2));
        Preco leiteM2 = precoMock(leite, m2, "4.50", LocalDate.now().minusDays(1));
        Preco arrozM1 = precoMock(arroz, m1, "20.00", LocalDate.now().minusDays(3));

        when(precoRepository.findByDataColetaBetween(any(), any()))
                .thenReturn(List.of(leiteM1, leiteM2, arrozM1));

        BigDecimal valor = dashboardService.calcularValorCesta();

        // 4.50 (menor leite) + 20.00 (único arroz) = 24.50
        assertThat(valor).isEqualByComparingTo(new BigDecimal("24.50"));
    }

    @Test
    void calcularVariacaoSemanal_quandoSemanaAnteriorEhZero_retornaZero() {
        when(precoRepository.findByDataColetaBetween(any(), any())).thenReturn(List.of());

        BigDecimal variacao = dashboardService.calcularVariacaoSemanal();

        assertThat(variacao).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void calcularVariacaoSemanal_quandoCestaCai_retornaPercentualNegativo() {
        Produto leite = produtoMock(1L, "Leite");
        Mercado m = mercadoMock(1L, "M");

        // semana anterior (8-14 dias atrás): R$ 10
        Preco anterior = precoMock(leite, m, "10.00", LocalDate.now().minusDays(10));
        // semana atual (0-7 dias atrás): R$ 8
        Preco atual = precoMock(leite, m, "8.00", LocalDate.now().minusDays(2));

        when(precoRepository.findByDataColetaBetween(
                LocalDate.now().minusDays(7), LocalDate.now()))
                .thenReturn(List.of(atual));
        when(precoRepository.findByDataColetaBetween(
                LocalDate.now().minusDays(14), LocalDate.now().minusDays(8)))
                .thenReturn(List.of(anterior));

        BigDecimal variacao = dashboardService.calcularVariacaoSemanal();

        // (8 - 10) / 10 * 100 = -20%
        assertThat(variacao).isEqualByComparingTo(new BigDecimal("-20.00"));
    }

    @Test
    void calcularEconomiaMedia_calculaPorProdutoEMedia() {
        Produto leite = produtoMock(1L, "Leite");
        Produto arroz = produtoMock(2L, "Arroz");
        Mercado m1 = mercadoMock(1L, "M1");
        Mercado m2 = mercadoMock(2L, "M2");

        // Leite: maior 10, menor 8 → economia 20%
        // Arroz: maior 25, menor 20 → economia 20%
        // Média: 20%
        when(precoRepository.findByDataColetaBetween(any(), any())).thenReturn(List.of(
                precoMock(leite, m1, "10.00", LocalDate.now().minusDays(5)),
                precoMock(leite, m2, "8.00",  LocalDate.now().minusDays(5)),
                precoMock(arroz, m1, "25.00", LocalDate.now().minusDays(5)),
                precoMock(arroz, m2, "20.00", LocalDate.now().minusDays(5))
        ));

        BigDecimal economia = dashboardService.calcularEconomiaMedia();

        assertThat(economia).isEqualByComparingTo(new BigDecimal("20.00"));
    }

    @Test
    void calcularEconomiaMedia_quandoProdutoTemUmSoMercado_naoEntraNaMedia() {
        Produto leite = produtoMock(1L, "Leite");
        Mercado m1 = mercadoMock(1L, "M1");

        when(precoRepository.findByDataColetaBetween(any(), any())).thenReturn(List.of(
                precoMock(leite, m1, "10.00", LocalDate.now().minusDays(5))
        ));

        BigDecimal economia = dashboardService.calcularEconomiaMedia();

        assertThat(economia).isEqualByComparingTo(BigDecimal.ZERO);
    }

    private Produto produtoMock(Long id, String nome) {
        Produto p = new Produto();
        p.setNome(nome);
        try {
            java.lang.reflect.Field f = Produto.class.getDeclaredField("id");
            f.setAccessible(true);
            f.set(p, id);
        } catch (Exception e) { throw new RuntimeException(e); }
        return p;
    }

    private Mercado mercadoMock(Long id, String nome) {
        Mercado m = new Mercado();
        m.setNomeFantasia(nome);
        try {
            java.lang.reflect.Field f = Mercado.class.getDeclaredField("id");
            f.setAccessible(true);
            f.set(m, id);
        } catch (Exception e) { throw new RuntimeException(e); }
        return m;
    }

    private Preco precoMock(Produto p, Mercado m, String valor, LocalDate data) {
        Preco preco = new Preco();
        preco.setProduto(p);
        preco.setMercado(m);
        preco.setValor(new BigDecimal(valor));
        preco.setDataColeta(data);
        preco.setStatus(StatusPreco.CONFIRMADO);
        return preco;
    }
}