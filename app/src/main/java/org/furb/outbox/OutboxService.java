package org.furb.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.furb.model.OutboxEvent;
import org.furb.repositories.OutboxEventRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class OutboxService {

    private final OutboxEventRepository repository;
    private final ObjectMapper objectMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final Clock clock;

    public OutboxService(OutboxEventRepository repository,
                         ObjectMapper objectMapper,
                         ApplicationEventPublisher eventPublisher,
                         Clock clock) {
        this.repository = repository;
        this.objectMapper = objectMapper;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    /**
     * Grava o evento na outbox dentro da transação corrente (mesmo commit da operação de domínio).
     * Retorna o eventoId (UUID) para o chamador embutir no payload, se quiser.
     */
    @Transactional
    public String registrar(String eventoId, String routingKey, Object payload) {
        try {
            String json = objectMapper.writeValueAsString(payload);
            OutboxEvent evento = new OutboxEvent(eventoId, routingKey, json, LocalDateTime.now(clock));
            OutboxEvent salvo = repository.save(evento);
            // dispara publish imediato APÓS o commit (rede rápida); relay agendado é o fallback
            eventPublisher.publishEvent(new OutboxRegistradoEvent(salvo.getId()));
            return eventoId;
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new IllegalStateException("Falha ao serializar payload da outbox", e);
        }
    }

    public String novoEventoId() {
        return UUID.randomUUID().toString();
    }
}