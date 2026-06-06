package org.furb.repositories;

import jakarta.persistence.QueryHint;
import org.furb.model.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {

    Optional<OutboxEvent> findByEventoId(String eventoId);

    @Query(value = "SELECT * FROM outbox WHERE enviado = false ORDER BY criado_em "
            + "LIMIT :limite FOR UPDATE SKIP LOCKED", nativeQuery = true)
    List<OutboxEvent> lockBatchPendente(@Param("limite") int limite);

    @Modifying
    @Query("DELETE FROM OutboxEvent o WHERE o.enviado = true AND o.enviadoEm < :limite")
    int apagarEnviadosAntesDe(@Param("limite") LocalDateTime limite);
}