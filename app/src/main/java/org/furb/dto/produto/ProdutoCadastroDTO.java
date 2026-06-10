package org.furb.dto.produto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ProdutoCadastroDTO {

    @NotBlank(message = "Nome do produto é obrigatório")
    @Size(max = 150, message = "Nome do produto deve ter no máximo 150 caracteres")
    private String nome;

    @Size(max = 50, message = "Código de barras deve ter no máximo 50 caracteres")
    private String codigoBarras;

    @Size(max = 100, message = "Marca deve ter no máximo 100 caracteres")
    private String marca;

    @Size(max = 30, message = "Unidade de medida deve ter no máximo 30 caracteres")
    private String unidadeMedida;

    @NotNull(message = "Categoria é obrigatória")
    private Long categoriaId;

    public ProdutoCadastroDTO() {
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCodigoBarras() {
        return codigoBarras;
    }

    public void setCodigoBarras(String codigoBarras) {
        this.codigoBarras = codigoBarras;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getUnidadeMedida() {
        return unidadeMedida;
    }

    public void setUnidadeMedida(String unidadeMedida) {
        this.unidadeMedida = unidadeMedida;
    }

    public Long getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(Long categoriaId) {
        this.categoriaId = categoriaId;
    }
}