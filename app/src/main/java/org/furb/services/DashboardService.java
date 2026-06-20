package org.furb.services;

import org.furb.dto.dashboard.HistoricoPrecoDTO;
import org.furb.dto.dashboard.KpiDashboardDTO;
import org.furb.dto.dashboard.PontoSparklineDTO;
import org.furb.dto.dashboard.ProdutoDashboardDTO;
import org.furb.enums.StatusPreco;
import org.furb.model.Mercado;
import org.furb.model.Preco;
import org.furb.model.Produto;
import org.furb.services.exeptions.ResourceNotFoundException;
import org.furb.repositories.MercadoRepository;
import org.furb.repositories.PrecoRepository;
import org.furb.repositories.ProdutoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    /**
     * Status considerados nas métricas/gráfico do dashboard. PENDENTE (não validado) e
     * REJEITADO ficam de fora para não distorcer cesta, variação, economia e sparkline.
     */
    private static final List<StatusPreco> STATUS_VALIDOS =
            List.of(StatusPreco.CONFIRMADO, StatusPreco.DESATUALIZADO);

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
        List<Preco> precos = precoRepository.findByDataColetaBetweenAndStatusIn(inicio, fim, STATUS_VALIDOS);
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
        List<Preco> precos = precoRepository.findByDataColetaBetweenAndStatusIn(inicio, fim, STATUS_VALIDOS);

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

    public List<PontoSparklineDTO> montarSparkline(Long produtoId, int dias) {
        LocalDate fim = LocalDate.now();
        LocalDate inicio = fim.minusDays(dias - 1L);

        List<Preco> precos = precoRepository
                .findByProdutoIdAndDataColetaBetweenAndStatusIn(produtoId, inicio, fim, STATUS_VALIDOS);

        if (precos.isEmpty()) return List.of();

        Map<LocalDate, BigDecimal> mediaPorDia = precos.stream()
                .collect(Collectors.groupingBy(
                        Preco::getDataColeta,
                        Collectors.mapping(Preco::getValor,
                                Collectors.collectingAndThen(Collectors.toList(), this::media))));

        List<PontoSparklineDTO> resultado = new ArrayList<>();
        BigDecimal ultimoConhecido = null;

        for (int i = 0; i < dias; i++) {
            LocalDate dia = inicio.plusDays(i);
            BigDecimal valor = mediaPorDia.get(dia);
            if (valor != null) {
                ultimoConhecido = valor;
            }
            if (ultimoConhecido != null) {
                resultado.add(new PontoSparklineDTO(dia, ultimoConhecido));
            }
        }

        return resultado;
    }

    public HistoricoPrecoDTO montarHistorico(Long produtoId, int dias) {
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Produto não encontrado: id=" + produtoId));

        return new HistoricoPrecoDTO(
                produto.getId(),
                produto.getNome(),
                montarSparkline(produtoId, dias)
        );
    }

    public Page<ProdutoDashboardDTO> listarProdutosDashboard(String ordem, Pageable pageable) {
        List<Produto> produtos = produtoRepository.findAll().stream()
                .filter(p -> Boolean.TRUE.equals(p.getAtivo()))
                .toList();

        List<ProdutoDashboardDTO> dtos = produtos.stream()
                .map(this::montarProdutoDashboard)
                .filter(Objects::nonNull)
                .toList();

        Comparator<ProdutoDashboardDTO> comparator = switch (ordem) {
            case "quedas" -> Comparator.comparing(ProdutoDashboardDTO::getTendenciaPercentual);
            case "altas"  -> Comparator.comparing(ProdutoDashboardDTO::getTendenciaPercentual).reversed();
            default       -> Comparator.comparing(ProdutoDashboardDTO::getNome);
        };

        List<ProdutoDashboardDTO> ordenado = dtos.stream().sorted(comparator).toList();

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), ordenado.size());
        List<ProdutoDashboardDTO> pageContent = start >= ordenado.size()
                ? List.of()
                : ordenado.subList(start, end);

        return new PageImpl<>(pageContent, pageable, ordenado.size());
    }

    private ProdutoDashboardDTO montarProdutoDashboard(Produto p) {
        List<PontoSparklineDTO> sparkline = montarSparkline(p.getId(), 30);
        if (sparkline.isEmpty()) return null;

        LocalDate fim = LocalDate.now();
        LocalDate inicio = fim.minusDays(30);
        List<Preco> precos = precoRepository
                .findByProdutoIdAndDataColetaBetweenAndStatusIn(p.getId(), inicio, fim, STATUS_VALIDOS);

        Preco menor = precos.stream()
                .min(Comparator.comparing(Preco::getValor))
                .orElseThrow();

        Mercado mercadoMenor = menor.getMercado();
        BigDecimal tendencia = calcularTendenciaPercentual(sparkline);

        return new ProdutoDashboardDTO(
                p.getId(),
                p.getNome(),
                p.getMarca(),
                p.getUnidadeMedida(),
                p.getCategoria() != null ? p.getCategoria().getNome() : null,
                p.getImagemPath(),
                menor.getValor(),
                mercadoMenor.getNomeFantasia(),
                mercadoMenor.getId(),
                tendencia,
                sparkline
        );
    }

    private BigDecimal calcularTendenciaPercentual(List<PontoSparklineDTO> sparkline) {
        if (sparkline.size() < 2) return BigDecimal.ZERO;
        BigDecimal primeiro = sparkline.get(0).getValor();
        BigDecimal ultimo = sparkline.get(sparkline.size() - 1).getValor();
        if (primeiro.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
        return ultimo.subtract(primeiro)
                .divide(primeiro, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"))
                .setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal media(List<BigDecimal> valores) {
        if (valores.isEmpty()) return BigDecimal.ZERO;
        BigDecimal soma = valores.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        return soma.divide(new BigDecimal(valores.size()), 2, RoundingMode.HALF_UP);
    }
}