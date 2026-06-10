package org.furb.messaging.contract;

public record DenunciaResolvidaEvent(String eventoId, Long denunciaId, Long denuncianteId, String status) {
}