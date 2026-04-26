package org.furb.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "mercado_comerciantes",
        uniqueConstraints = @UniqueConstraint(columnNames = {"mercado_id", "comerciante_id"})
)
public class MercadoComerciante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mercado_id", nullable = false)
    private Mercado mercado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comerciante_id", nullable = false)
    private Usuario comerciante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vinculado_por", nullable = false)
    private Usuario vinculadoPor;

    @Column(nullable = false)
    private LocalDateTime dataVinculacao = LocalDateTime.now();

    public MercadoComerciante() {
    }

    public Long getId() {
        return id;
    }

    public Mercado getMercado() {
        return mercado;
    }

    public void setMercado(Mercado mercado) {
        this.mercado = mercado;
    }

    public Usuario getComerciante() {
        return comerciante;
    }

    public void setComerciante(Usuario comerciante) {
        this.comerciante = comerciante;
    }

    public Usuario getVinculadoPor() {
        return vinculadoPor;
    }

    public void setVinculadoPor(Usuario vinculadoPor) {
        this.vinculadoPor = vinculadoPor;
    }

    public LocalDateTime getDataVinculacao() {
        return dataVinculacao;
    }

    public void setDataVinculacao(LocalDateTime dataVinculacao) {
        this.dataVinculacao = dataVinculacao;
    }
}