package org.furb.rabbitmqworker;

import org.furb.enums.AlvoFoto;

public interface FotoAlvoHandler {
    AlvoFoto tipo();
    /** true se o alvo existe e ainda não está PROCESSADO (idempotência). */
    boolean pendente(Long alvoId);
    /** Grava os caminhos processados no alvo e marca como PROCESSADO. */
    void aplicar(Long alvoId, String fotoPath, String thumbPath);
}