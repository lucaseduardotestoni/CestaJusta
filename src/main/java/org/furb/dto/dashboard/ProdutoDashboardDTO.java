package org.furb.dto.dashboard;

import java.math.BigDecimal;
import java.util.List;

public class ProdutoDashboardDTO {

    private Long id;
    private String nome;
    private String marca;
    private String unidadeMedida;
    private String categoria;
    private String imagemPath;
    private BigDecimal menorPreco;
    private String mercadoMenorNome;
    private Long mercadoMenorId;
    private BigDecimal tendenciaPercentual;
    private List<PontoSparklineDTO> sparkline;

    public ProdutoDashboardDTO() {
    }

    public ProdutoDashboardDTO(Long id, String nome, String marca, String unidadeMedida,
                               String categoria, String imagemPath, BigDecimal menorPreco,
                               String mercadoMenorNome, Long mercadoMenorId,
                               BigDecimal tendenciaPercentual, List<PontoSparklineDTO> sparkline) {
        this.id = id;
        this.nome = nome;
        this.marca = marca;
        this.unidadeMedida = unidadeMedida;
        this.categoria = categoria;
        this.imagemPath = imagemPath;
        this.menorPreco = menorPreco;
        this.mercadoMenorNome = mercadoMenorNome;
        this.mercadoMenorId = mercadoMenorId;
        this.tendenciaPercentual = tendenciaPercentual;
        this.sparkline = sparkline;
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getMarca() { return marca; }
    public String getUnidadeMedida() { return unidadeMedida; }
    public String getCategoria() { return categoria; }
    public String getImagemPath() { return imagemPath; }
    public BigDecimal getMenorPreco() { return menorPreco; }
    public String getMercadoMenorNome() { return mercadoMenorNome; }
    public Long getMercadoMenorId() { return mercadoMenorId; }
    public BigDecimal getTendenciaPercentual() { return tendenciaPercentual; }
    public List<PontoSparklineDTO> getSparkline() { return sparkline; }
}