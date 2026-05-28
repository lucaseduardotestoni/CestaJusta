package org.furb.dto.categoria;

public class CategoriaResponseDTO {

    private final Long id;
    private final String nome;

    public CategoriaResponseDTO(Long id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }
}