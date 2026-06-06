package org.furb.messaging.contract;

public final class RoutingKeys {

    public static final String EXCHANGE = "cestajusta.events";
    public static final String DLX = "cestajusta.dlx";

    public static final String FOTO_SOLICITADA = "denuncia.foto.solicitada";
    public static final String DENUNCIA_APROVADA = "denuncia.aprovada";
    public static final String DENUNCIA_REJEITADA = "denuncia.rejeitada";
    public static final String PRECO_REJEITADO = "preco.rejeitado";

    public static final String FILA_FOTO = "foto.processar";
    public static final String FILA_NOTIFICACAO = "notificacao";
    public static final String FILA_FOTO_DLQ = "foto.processar.dlq";
    public static final String FILA_NOTIFICACAO_DLQ = "notificacao.dlq";

    private RoutingKeys() {
    }
}