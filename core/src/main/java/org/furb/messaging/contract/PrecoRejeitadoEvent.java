package org.furb.messaging.contract;

public record PrecoRejeitadoEvent(String eventoId, Long precoId, Long autorId) {
}