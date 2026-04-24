package org.furb.dto.mercado;

public class MercadoResponseDTO {

    private Long id;
    private String nomeFantasia;
    private String cnpj;
    private String cidade;
    private String estado;
    private Boolean ativo;

    public MercadoResponseDTO() {
    }

    public MercadoResponseDTO(Long id, String nomeFantasia, String cnpj,
                              String cidade, String estado, Boolean ativo) {
        this.id = id;
        this.nomeFantasia = nomeFantasia;
        this.cnpj = cnpj;
        this.cidade = cidade;
        this.estado = estado;
        this.ativo = ativo;
    }

    public Long getId() {
        return id;
    }

    public String getNomeFantasia() {
        return nomeFantasia;
    }

    public String getCnpj() {
        return cnpj;
    }

    public String getCidade() {
        return cidade;
    }

    public String getEstado() {
        return estado;
    }

    public Boolean getAtivo() {
        return ativo;
    }
}