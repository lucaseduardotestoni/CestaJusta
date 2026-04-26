package org.furb.dto.comparacao;

import java.math.BigDecimal;
import java.util.List;

public class ComparacaoPrecoDTO {

    private Long produtoId;
    private String produtoNome;
    private String produtoMarca;
    private String produtoUnidadeMedida;
    private String produtoCategoria;
    private BigDecimal menorPreco;
    private BigDecimal maiorPreco;
    private BigDecimal precoMedio;
    private int totalMercados;
    private List<PrecoPorMercadoDTO> precosPorMercado;

    public ComparacaoPrecoDTO() {
    }

    public ComparacaoPrecoDTO(Long produtoId, String produtoNome, String produtoMarca,
                              String produtoUnidadeMedida, String produtoCategoria,
                              BigDecimal menorPreco, BigDecimal maiorPreco, BigDecimal precoMedio,
                              int totalMercados, List<PrecoPorMercadoDTO> precosPorMercado) {
        this.produtoId = produtoId;
        this.produtoNome = produtoNome;
        this.produtoMarca = produtoMarca;
        this.produtoUnidadeMedida = produtoUnidadeMedida;
        this.produtoCategoria = produtoCategoria;
        this.menorPreco = menorPreco;
        this.maiorPreco = maiorPreco;
        this.precoMedio = precoMedio;
        this.totalMercados = totalMercados;
        this.precosPorMercado = precosPorMercado;
    }

    public Long getProdutoId() {
        return produtoId;
    }

    public String getProdutoNome() {
        return produtoNome;
    }

    public String getProdutoMarca() {
        return produtoMarca;
    }

    public String getProdutoUnidadeMedida() {
        return produtoUnidadeMedida;
    }

    public String getProdutoCategoria() {
        return produtoCategoria;
    }

    public BigDecimal getMenorPreco() {
        return menorPreco;
    }

    public BigDecimal getMaiorPreco() {
        return maiorPreco;
    }

    public BigDecimal getPrecoMedio() {
        return precoMedio;
    }

    public int getTotalMercados() {
        return totalMercados;
    }

    public List<PrecoPorMercadoDTO> getPrecosPorMercado() {
        return precosPorMercado;
    }
}