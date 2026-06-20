package org.furb.enums;

/** Por que o usuário autenticado não pode votar numa denúncia. null/ausente = pode votar. */
public enum MotivoBloqueioVoto {
    DENUNCIANTE,
    DONO_MERCADO,
    JA_RESOLVIDA
}
