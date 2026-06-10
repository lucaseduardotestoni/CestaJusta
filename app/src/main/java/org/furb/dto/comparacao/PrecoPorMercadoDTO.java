package org.furb.dto.comparacao;

import org.furb.enums.StatusPreco;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PrecoPorMercadoDTO {

    private Long mercadoId;
    private String mercadoNomeFantasia;
    private String cidade;
    private String estado;
    private BigDecimal valor;
    private LocalDate dataColeta;
    private StatusPreco status;

    public PrecoPorMercadoDTO() {
    }

    public PrecoPorMercadoDTO(Long mercadoId, String mercadoNomeFantasia,
                              String cidade, String estado,
                              BigDecimal valor, LocalDate dataColeta, StatusPreco status) {
        this.mercadoId = mercadoId;
        this.mercadoNomeFantasia = mercadoNomeFantasia;
        this.cidade = cidade;
        this.estado = estado;
        this.valor = valor;
        this.dataColeta = dataColeta;
        this.status = status;
    }

    public Long getMercadoId() {
        return mercadoId;
    }

    public String getMercadoNomeFantasia() {
        return mercadoNomeFantasia;
    }

    public String getCidade() {
        return cidade;
    }

    public String getEstado() {
        return estado;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public LocalDate getDataColeta() {
        return dataColeta;
    }

    public StatusPreco getStatus() {
        return status;
    }
}