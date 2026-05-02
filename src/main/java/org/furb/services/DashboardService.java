package org.furb.services;

import org.furb.model.Preco;
import org.furb.repositories.MercadoRepository;
import org.furb.repositories.PrecoRepository;
import org.furb.repositories.ProdutoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
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
        LocalDate fim = LocalDate.now();
        LocalDate inicio = fim.minusDays(7);
        List<Preco> precos = precoRepository.findByDataColetaBetween(inicio, fim);

        if (precos.isEmpty()) {
            return BigDecimal.ZERO;
        }

        Map<Long, BigDecimal> menorPorProduto = precos.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getProduto().getId(),
                        Collectors.mapping(Preco::getValor,
                                Collectors.minBy(BigDecimal::compareTo))))
                .entrySet().stream()
                .filter(e -> e.getValue().isPresent())
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().get()));

        return menorPorProduto.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}