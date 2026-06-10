package org.furb.outbox;

import org.furb.messaging.contract.RoutingKeys;
import org.furb.model.OutboxEvent;
import org.furb.repositories.OutboxEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OutboxRelayTest {

    @Mock private OutboxEventRepository repository;
    @Mock private RabbitTemplate rabbitTemplate;

    private final Clock clock = Clock.fixed(Instant.parse("2026-06-09T12:00:00Z"), ZoneId.of("UTC"));
    private OutboxRelay relay;

    @BeforeEach
    void setUp() {
        relay = new OutboxRelay(repository, rabbitTemplate, clock);
    }

    private OutboxEvent evento(String rk, String payload, boolean enviado) {
        OutboxEvent e = new OutboxEvent("evt", rk, payload, LocalDateTime.now(clock));
        e.setEnviado(enviado);
        return e;
    }

    @Test
    void publicarUm_pendente_publicaMarcaEnviadoESalva() {
        OutboxEvent e = evento("rk.x", "{\"a\":1}", false);
        when(repository.findById(1L)).thenReturn(Optional.of(e));

        relay.publicarUm(1L);

        verify(rabbitTemplate).convertAndSend(eq(RoutingKeys.EXCHANGE), eq("rk.x"), eq((Object) "{\"a\":1}"));
        assertThat(e.isEnviado()).isTrue();
        assertThat(e.getEnviadoEm()).isEqualTo(LocalDateTime.now(clock));
        verify(repository).save(e);
    }

    @Test
    void publicarUm_jaEnviado_naoRepublica() {
        when(repository.findById(1L)).thenReturn(Optional.of(evento("rk.x", "{}", true)));

        relay.publicarUm(1L);

        verify(rabbitTemplate, never()).convertAndSend(eq(RoutingKeys.EXCHANGE), eq("rk.x"), eq((Object) "{}"));
        verify(repository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void publicarUm_inexistente_naoFazNada() {
        when(repository.findById(9L)).thenReturn(Optional.empty());

        relay.publicarUm(9L);

        verify(repository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void aoRegistrar_publicaImediatamente() {
        OutboxEvent e = evento("rk.x", "{}", false);
        when(repository.findById(3L)).thenReturn(Optional.of(e));

        relay.aoRegistrar(new OutboxRegistradoEvent(3L));

        assertThat(e.isEnviado()).isTrue();
    }

    @Test
    void aoRegistrar_brokerFora_engoleExcecaoEDeixaParaORelay() {
        when(repository.findById(4L)).thenThrow(new RuntimeException("broker down"));

        assertThatCode(() -> relay.aoRegistrar(new OutboxRegistradoEvent(4L)))
                .doesNotThrowAnyException();
    }

    @Test
    void relay_loteVazio_naoFazNada() {
        when(repository.lockBatchPendente(anyInt())).thenReturn(List.of());

        relay.relay();

        verify(repository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void relay_publicaTodosOsPendentes() {
        OutboxEvent a = evento("rk.a", "{\"a\":1}", false);
        OutboxEvent b = evento("rk.b", "{\"b\":1}", false);
        when(repository.lockBatchPendente(anyInt())).thenReturn(List.of(a, b));

        relay.relay();

        assertThat(a.isEnviado()).isTrue();
        assertThat(b.isEnviado()).isTrue();
    }

    @Test
    void relay_falhaNoBroker_incrementaTentativasENaoMarcaEnviado() {
        OutboxEvent falho = evento("rk.fail", "{\"fail\":1}", false);
        when(repository.lockBatchPendente(anyInt())).thenReturn(List.of(falho));
        doThrow(new RuntimeException("broker down"))
                .when(rabbitTemplate).convertAndSend(anyString(), anyString(), any(Object.class));

        relay.relay();

        assertThat(falho.isEnviado()).isFalse();
        assertThat(falho.getTentativas()).isEqualTo(1);
    }
}