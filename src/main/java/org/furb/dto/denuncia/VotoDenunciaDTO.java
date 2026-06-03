package org.furb.dto.denuncia;

import jakarta.validation.constraints.NotNull;
import org.furb.enums.TipoVoto;

public class VotoDenunciaDTO {

    @NotNull(message = "tipo é obrigatório")
    private TipoVoto tipo;

    public TipoVoto getTipo() {
        return tipo;
    }

    public void setTipo(TipoVoto tipo) {
        this.tipo = tipo;
    }
}