package org.furb.messaging.contract;

import org.furb.enums.AlvoFoto;

public record FotoSolicitadaEvent(String eventoId, AlvoFoto tipo, Long alvoId, String fotoPathOriginal) {
}