package org.furb.jobs;

import org.furb.repositories.OutboxEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OutboxLimpezaJobTest {

    @Mock private OutboxEventRepository repository;

    private final Clock clock = Clock.fixed(Instant.parse("2026-06-09T12:00:00Z"), ZoneId.of("UTC"));
    private OutboxLimpezaJob job;

    @BeforeEach
    void setUp() {
        job = new OutboxLimpezaJob(repository, clock);
    }

    @Test
    void limpar_apagaEnviadosComMaisDe7Dias() {
        LocalDateTime limiteEsperado = LocalDateTime.now(clock).minusDays(7);
        when(repository.apagarEnviadosAntesDe(limiteEsperado)).thenReturn(5);

        job.limpar();

        verify(repository).apagarEnviadosAntesDe(limiteEsperado);
    }

    @Test
    void limpar_semEnviadosAntigos_naoFalha() {
        when(repository.apagarEnviadosAntesDe(LocalDateTime.now(clock).minusDays(7))).thenReturn(0);

        job.limpar();

        verify(repository).apagarEnviadosAntesDe(LocalDateTime.now(clock).minusDays(7));
    }
}