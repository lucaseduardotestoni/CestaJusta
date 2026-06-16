package org.furb.dto.usuario;

import org.furb.enums.TipoUsuario;

public class UsuarioMeDTO {

    private final String nome;
    private final String email;
    private final TipoUsuario tipoUsuario;

    public UsuarioMeDTO(String nome, String email, TipoUsuario tipoUsuario) {
        this.nome = nome;
        this.email = email;
        this.tipoUsuario = tipoUsuario;
    }

    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public TipoUsuario getTipoUsuario() { return tipoUsuario; }
}
