package org.furb.services;

import org.furb.dto.dashboard.KpiDashboardDTO;
import org.furb.model.Preco;
import org.furb.repositories.MercadoRepository;
import org.furb.repositories.PrecoRepository;
import org.furb.repositories.ProdutoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final PrecoRepository precoRepository;
    private final ProdutoRepository produtoRepository;
    private final MercadoRepository mercadoRepository;

    public DashboardService(PrecoRepository precoRepository,
                            ProdutoRepository produtoRepository,
                            MercadoRepository mercadoRepository) {
        this.precoRepository = precoRepository;
        this.produtoRepository = produtoRepository;
        this.mercadoRepository = mercadoRepository;
    }

    public BigDecimal calcularValorCesta() {
        return calcularValorCestaNoIntervalo(LocalDate.now().minusDays(7), LocalDate.now());
    }

    private BigDecimal calcularValorCestaNoIntervalo(LocalDate inicio, LocalDate fim) {
        List<Preco> precos = precoRepository.findByDataColetaBetween(inicio, fim);
        if (precos.isEmpty()) return BigDecimal.ZERO;

        return precos.stream()
                .collect(Collectors.groupingBy(p -> p.getProduto().getId(),
                        Collectors.mapping(Preco::getValor,
                                Collectors.minBy(BigDecimal::compareTo))))
                .values().stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal calcularVariacaoSemanal() {
        BigDecimal atual = calcularValorCestaNoIntervalo(LocalDate.now().minusDays(7), LocalDate.now());
        BigDecimal anterior = calcularValorCestaNoIntervalo(
                LocalDate.now().minusDays(14), LocalDate.now().minusDays(8));

        if (anterior.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        return atual.subtract(anterior)
                .divide(anterior, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"))
                .setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calcularEconomiaMedia() {
        LocalDate fim = LocalDate.now();
        LocalDate inicio = fim.minusDays(30);
        List<Preco> precos = precoRepository.findByDataColetaBetween(inicio, fim);

        Map<Long, List<BigDecimal>> precosPorProduto = precos.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getProduto().getId(),
                        Collectors.mapping(Preco::getValor, Collectors.toList())));

        List<BigDecimal> economias = precosPorProduto.values().stream()
                .filter(lista -> lista.size() >= 2)
                .map(lista -> {
                    BigDecimal maior = lista.stream().max(BigDecimal::compareTo).get();
                    BigDecimal menor = lista.stream().min(BigDecimal::compareTo).get();
                    return maior.subtract(menor)
                            .divide(maior, 4, RoundingMode.HALF_UP)
                            .multiply(new BigDecimal("100"));
                })
                .toList();

        if (economias.isEmpty()) return BigDecimal.ZERO;

        BigDecimal soma = economias.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        return soma.divide(new BigDecimal(economias.size()), 2, RoundingMode.HALF_UP);
    }

    public KpiDashboardDTO montarKpis() {
        return new KpiDashboardDTO(
                calcularValorCesta(),
                calcularVariacaoSemanal(),
                (int) produtoRepository.count(),
                (int) mercadoRepository.count(),
                calcularEconomiaMedia()
        );
    }
}