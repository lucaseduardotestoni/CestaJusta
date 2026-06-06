package org.furb.dto.dashboard;

import java.math.BigDecimal;

public class KpiDashboardDTO {

    private BigDecimal valorCesta;
    private BigDecimal variacaoSemanal;
    private int totalProdutos;
    private int totalMercados;
    private BigDecimal economiaMedia;

    public KpiDashboardDTO() {
    }

    public KpiDashboardDTO(BigDecimal valorCesta, BigDecimal variacaoSemanal,
                           int totalProdutos, int totalMercados, BigDecimal economiaMedia) {
        this.valorCesta = valorCesta;
        this.variacaoSemanal = variacaoSemanal;
        this.totalProdutos = totalProdutos;
        this.totalMercados = totalMercados;
        this.economiaMedia = economiaMedia;
    }

    public BigDecimal getValorCesta() { return valorCesta; }
    public void setValorCesta(BigDecimal v) { this.valorCesta = v; }

    public BigDecimal getVariacaoSemanal() { return variacaoSemanal; }
    public void setVariacaoSemanal(BigDecimal v) { this.variacaoSemanal = v; }

    public int getTotalProdutos() { return totalProdutos; }
    public void setTotalProdutos(int v) { this.totalProdutos = v; }

    public int getTotalMercados() { return totalMercados; }
    public void setTotalMercados(int v) { this.totalMercados = v; }

    public BigDecimal getEconomiaMedia() { return economiaMedia; }
    public void setEconomiaMedia(BigDecimal v) { this.economiaMedia = v; }
}