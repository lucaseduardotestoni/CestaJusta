package org.furb.dto.denuncia;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class DenunciaCadastroDTO {

    @NotNull(message = "precoId é obrigatório")
    private Long precoId;

    @NotBlank(message = "Motivo é obrigatório")
    @Size(max = 255, message = "Motivo deve ter no máximo 255 caracteres")
    private String motivo;

    @Size(max = 1000, message = "Descrição deve ter no máximo 1000 caracteres")
    private String descricao;

    public Long getPrecoId() {
        return precoId;
    }

    public void setPrecoId(Long precoId) {
        this.precoId = precoId;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
