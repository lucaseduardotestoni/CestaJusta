package org.furb.jobs;

import org.furb.repositories.OutboxEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;

@Component
public class OutboxLimpezaJob {

    private static final Logger log = LoggerFactory.getLogger(OutboxLimpezaJob.class);
    private static final int DIAS_RETENCAO = 7;

    private final OutboxEventRepository repository;
    private final Clock clock;

    public OutboxLimpezaJob(OutboxEventRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    @Scheduled(cron = "0 30 3 * * *")
    @Transactional
    public void limpar() {
        LocalDateTime limite = LocalDateTime.now(clock).minusDays(DIAS_RETENCAO);
        int apagados = repository.apagarEnviadosAntesDe(limite);
        if (apagados > 0) {
            log.info("Limpeza outbox: {} evento(s) enviado(s) removido(s)", apagados);
        }
    }
}