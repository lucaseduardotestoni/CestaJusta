package org.furb.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "outbox", indexes = {
        @Index(name = "idx_outbox_pendente", columnList = "criado_em")
})
public class OutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "evento_id", nullable = false, unique = true, length = 36)
    private String eventoId;

    @Column(name = "routing_key", nullable = false, length = 100)
    private String routingKey;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String payload;

    @Column(nullable = false)
    private boolean enviado = false;

    @Column(nullable = false)
    private int tentativas = 0;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;

    @Column(name = "enviado_em")
    private LocalDateTime enviadoEm;

    public OutboxEvent() {
    }

    public OutboxEvent(String eventoId, String routingKey, String payload, LocalDateTime criadoEm) {
        this.eventoId = eventoId;
        this.routingKey = routingKey;
        this.payload = payload;
        this.criadoEm = criadoEm;
    }

    public Long getId() { return id; }
    public String getEventoId() { return eventoId; }
    public String getRoutingKey() { return routingKey; }
    public String getPayload() { return payload; }
    public boolean isEnviado() { return enviado; }
    public void setEnviado(boolean enviado) { this.enviado = enviado; }
    public int getTentativas() { return tentativas; }
    public void setTentativas(int tentativas) { this.tentativas = tentativas; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public LocalDateTime getEnviadoEm() { return enviadoEm; }
    public void setEnviadoEm(LocalDateTime enviadoEm) { this.enviadoEm = enviadoEm; }
}