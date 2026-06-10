package org.furb.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.furb.messaging.contract.FotoSolicitadaEvent;
import org.furb.model.OutboxEvent;
import org.furb.repositories.OutboxEventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OutboxServiceTest {

    @Mock private OutboxEventRepository repository;
    @Mock private ApplicationEventPublisher eventPublisher;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Clock clock = Clock.fixed(Instant.parse("2026-06-09T12:00:00Z"), ZoneId.of("UTC"));

    @Test
    void registrar_serializaSalvaEPublicaEventoInterno() {
        when(repository.save(any(OutboxEvent.class))).thenAnswer(i -> i.getArgument(0));
        OutboxService service = new OutboxService(repository, objectMapper, eventPublisher, clock);

        String id = service.registrar("evt-1", "denuncia.foto.solicitada",
                new FotoSolicitadaEvent("evt-1", 7L, "denuncias/foto.jpg"));

        assertThat(id).isEqualTo("evt-1");

        ArgumentCaptor<OutboxEvent> captor = ArgumentCaptor.forClass(OutboxEvent.class);
        verify(repository).save(captor.capture());
        OutboxEvent salvo = captor.getValue();
        assertThat(salvo.getEventoId()).isEqualTo("evt-1");
        assertThat(salvo.getRoutingKey()).isEqualTo("denuncia.foto.solicitada");
        assertThat(salvo.getPayload()).contains("denuncias/foto.jpg");
        assertThat(salvo.getCriadoEm()).isEqualTo(java.time.LocalDateTime.now(clock));

        verify(eventPublisher).publishEvent(any(OutboxRegistradoEvent.class));
    }

    @Test
    void registrar_payloadNaoSerializavel_lancaIllegalState() throws JsonProcessingException {
        ObjectMapper falho = mock(ObjectMapper.class);
        when(falho.writeValueAsString(any())).thenThrow(new JsonProcessingException("boom") {});
        OutboxService service = new OutboxService(repository, falho, eventPublisher, clock);

        assertThatThrownBy(() -> service.registrar("e", "rk", new Object()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("serializar");
    }

    @Test
    void novoEventoId_geraUuidValido() {
        OutboxService service = new OutboxService(repository, objectMapper, eventPublisher, clock);

        String id = service.novoEventoId();

        assertThat(UUID.fromString(id)).isNotNull();
    }
}
