package org.furb.dto.produto;

public class ProdutoResponseDTO {

    private Long id;
    private String nome;
    private String codigoBarras;
    private String marca;
    private String unidadeMedida;
    private String categoria;
    private Boolean ativo;

    public ProdutoResponseDTO() {
    }

    public ProdutoResponseDTO(Long id, String nome, String codigoBarras, String marca,
                              String unidadeMedida, String categoria, Boolean ativo) {
        this.id = id;
        this.nome = nome;
        this.codigoBarras = codigoBarras;
        this.marca = marca;
        this.unidadeMedida = unidadeMedida;
        this.categoria = categoria;
        this.ativo = ativo;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getCodigoBarras() {
        return codigoBarras;
    }

    public String getMarca() {
        return marca;
    }

    public String getUnidadeMedida() {
        return unidadeMedida;
    }

    public String getCategoria() {
        return categoria;
    }

    public Boolean getAtivo() {
        return ativo;
    }
}
