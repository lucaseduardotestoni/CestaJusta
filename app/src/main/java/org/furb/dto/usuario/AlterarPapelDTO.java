package org.furb.dto.usuario;

import jakarta.validation.constraints.NotNull;
import org.furb.enums.TipoUsuario;

public class AlterarPapelDTO {

    @NotNull(message = "Tipo de usuário é obrigatório")
    private TipoUsuario tipoUsuario;

    public AlterarPapelDTO() {
    }

    public TipoUsuario getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(TipoUsuario tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }
}