package org.furb.dto.mercadoComerciante;

import java.time.LocalDateTime;

public class MercadoComercianteResponseDTO {

    private Long id;
    private Long mercadoId;
    private String mercadoNomeFantasia;
    private Long comercianteId;
    private String comercianteNome;
    private String comercianteEmail;
    private Long vinculadoPorId;
    private String vinculadoPorNome;
    private LocalDateTime dataVinculacao;

    public MercadoComercianteResponseDTO() {
    }

    public MercadoComercianteResponseDTO(Long id,
                                         Long mercadoId, String mercadoNomeFantasia,
                                         Long comercianteId, String comercianteNome, String comercianteEmail,
                                         Long vinculadoPorId, String vinculadoPorNome,
                                         LocalDateTime dataVinculacao) {
        this.id = id;
        this.mercadoId = mercadoId;
        this.mercadoNomeFantasia = mercadoNomeFantasia;
        this.comercianteId = comercianteId;
        this.comercianteNome = comercianteNome;
        this.comercianteEmail = comercianteEmail;
        this.vinculadoPorId = vinculadoPorId;
        this.vinculadoPorNome = vinculadoPorNome;
        this.dataVinculacao = dataVinculacao;
    }

    public Long getId() {
        return id;
    }

    public Long getMercadoId() {
        return mercadoId;
    }

    public String getMercadoNomeFantasia() {
        return mercadoNomeFantasia;
    }

    public Long getComercianteId() {
        return comercianteId;
    }

    public String getComercianteNome() {
        return comercianteNome;
    }

    public String getComercianteEmail() {
        return comercianteEmail;
    }

    public Long getVinculadoPorId() {
        return vinculadoPorId;
    }

    public String getVinculadoPorNome() {
        return vinculadoPorNome;
    }

    public LocalDateTime getDataVinculacao() {
        return dataVinculacao;
    }
}