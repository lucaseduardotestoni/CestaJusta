package org.furb.messaging.contract;

public record FotoSolicitadaEvent(String eventoId, Long denunciaId, String fotoPathOriginal) {
}