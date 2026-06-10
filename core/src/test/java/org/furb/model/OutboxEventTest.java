package org.furb.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class OutboxEventTest {

    @Test
    void construtor_preencheCampos_eDefinePadroes() {
        LocalDateTime criadoEm = LocalDateTime.of(2026, 6, 9, 12, 0);

        OutboxEvent evento = new OutboxEvent("evt-1", "denuncia.foto.solicitada", "{\"x\":1}", criadoEm);

        assertThat(evento.getEventoId()).isEqualTo("evt-1");
        assertThat(evento.getRoutingKey()).isEqualTo("denuncia.foto.solicitada");
        assertThat(evento.getPayload()).isEqualTo("{\"x\":1}");
        assertThat(evento.getCriadoEm()).isEqualTo(criadoEm);
        assertThat(evento.isEnviado()).isFalse();
        assertThat(evento.getTentativas()).isZero();
        assertThat(evento.getEnviadoEm()).isNull();
    }

    @Test
    void setters_atualizamEstadoDeEnvio() {
        OutboxEvent evento = new OutboxEvent("evt-2", "rk", "{}", LocalDateTime.now());
        LocalDateTime enviadoEm = LocalDateTime.of(2026, 6, 9, 12, 5);

        evento.setEnviado(true);
        evento.setEnviadoEm(enviadoEm);
        evento.setTentativas(3);

        assertThat(evento.isEnviado()).isTrue();
        assertThat(evento.getEnviadoEm()).isEqualTo(enviadoEm);
        assertThat(evento.getTentativas()).isEqualTo(3);
    }
}