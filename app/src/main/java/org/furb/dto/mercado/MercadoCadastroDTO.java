package org.furb.dto.mercado;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class MercadoCadastroDTO {

    @NotBlank(message = "Nome fantasia é obrigatório")
    @Size(max = 150, message = "Nome fantasia deve ter no máximo 150 caracteres")
    private String nomeFantasia;

    @NotBlank(message = "CNPJ é obrigatório")
    @Size(max = 18, message = "CNPJ deve ter no máximo 18 caracteres")
    private String cnpj;

    @Size(max = 150, message = "Cidade deve ter no máximo 150 caracteres")
    private String cidade;

    @Size(max = 2, message = "Estado deve ter no máximo 2 caracteres")
    private String estado;

    public MercadoCadastroDTO() {
    }

    public String getNomeFantasia() {
        return nomeFantasia;
    }

    public void setNomeFantasia(String nomeFantasia) {
        this.nomeFantasia = nomeFantasia;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}