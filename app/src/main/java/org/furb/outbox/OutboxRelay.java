package org.furb.outbox;

import org.furb.messaging.contract.RoutingKeys;
import org.furb.model.OutboxEvent;
import org.furb.repositories.OutboxEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class OutboxRelay {

    private static final Logger log = LoggerFactory.getLogger(OutboxRelay.class);
    private static final int LOTE = 100;

    private final OutboxEventRepository repository;
    private final RabbitTemplate rabbitTemplate;
    private final Clock clock;

    public OutboxRelay(OutboxEventRepository repository, RabbitTemplate rabbitTemplate, Clock clock) {
        this.repository = repository;
        this.rabbitTemplate = rabbitTemplate;
        this.clock = clock;
    }

    /** Caminho feliz: publica logo após o commit da operação de domínio. */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void aoRegistrar(OutboxRegistradoEvent evento) {
        try {
            publicarUm(evento.outboxId());
        } catch (Exception e) {
            // broker fora? sem problema: a linha fica pendente e o relay agendado recupera
            log.warn("Publish imediato da outbox {} falhou; será reenviado pelo relay agendado: {}",
                    evento.outboxId(), e.getMessage());
        }
    }

    @Transactional
    public void publicarUm(Long outboxId) {
        repository.findById(outboxId)
                .filter(e -> !e.isEnviado())
                .ifPresent(this::publicar);
    }

    /** Rede de segurança: varre pendentes que não saíram no AFTER_COMMIT (ex.: broker estava fora). */
    @Scheduled(fixedDelayString = "${app.outbox.relay-delay-ms:45000}")
    @Transactional
    public void relay() {
        List<OutboxEvent> pendentes = repository.lockBatchPendente(LOTE);
        if (pendentes.isEmpty()) {
            return;
        }
        int ok = 0;
        for (OutboxEvent e : pendentes) {
            try {
                publicar(e);
                ok++;
            } catch (Exception ex) {
                e.setTentativas(e.getTentativas() + 1);
                log.warn("Falha ao publicar outbox {} (tentativa {}): {}",
                        e.getId(), e.getTentativas(), ex.getMessage());
            }
        }
        log.info("Relay outbox: {}/{} publicados", ok, pendentes.size());
    }

    private void publicar(OutboxEvent e) {
        rabbitTemplate.convertAndSend(RoutingKeys.EXCHANGE, e.getRoutingKey(), e.getPayload());
        e.setEnviado(true);
        e.setEnviadoEm(LocalDateTime.now(clock));
        repository.save(e);
    }
}