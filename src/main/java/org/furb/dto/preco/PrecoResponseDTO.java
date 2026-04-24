package org.furb.dto.preco;

import org.furb.enums.StatusPreco;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class PrecoResponseDTO {

    private Long id;
    private Long produtoId;
    private String produtoNome;
    private Long mercadoId;
    private String mercadoNomeFantasia;
    private Long usuarioId;
    private String usuarioNome;
    private BigDecimal valor;
    private LocalDate dataColeta;
    private StatusPreco status;
    private LocalDateTime dataCriacao;

    public PrecoResponseDTO() {
    }

    public PrecoResponseDTO(Long id, Long produtoId, String produtoNome,
                            Long mercadoId, String mercadoNomeFantasia,
                            Long usuarioId, String usuarioNome,
                            BigDecimal valor, LocalDate dataColeta,
                            StatusPreco status, LocalDateTime dataCriacao) {
        this.id = id;
        this.produtoId = produtoId;
        this.produtoNome = produtoNome;
        this.mercadoId = mercadoId;
        this.mercadoNomeFantasia = mercadoNomeFantasia;
        this.usuarioId = usuarioId;
        this.usuarioNome = usuarioNome;
        this.valor = valor;
        this.dataColeta = dataColeta;
        this.status = status;
        this.dataCriacao = dataCriacao;
    }

    public Long getId() {
        return id;
    }

    public Long getProdutoId() {
        return produtoId;
    }

    public String getProdutoNome() {
        return produtoNome;
    }

    public Long getMercadoId() {
        return mercadoId;
    }

    public String getMercadoNomeFantasia() {
        return mercadoNomeFantasia;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public String getUsuarioNome() {
        return usuarioNome;
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

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }
}