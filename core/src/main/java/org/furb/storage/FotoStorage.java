package org.furb.storage;

public interface FotoStorage {

    /** Grava o conteúdo em {subpasta}/{uuid}.{extensao} e retorna o caminho relativo gravado. */
    String store(String subpasta, byte[] conteudo, String extensao);

    /** Lê o conteúdo de um caminho relativo previamente retornado por store(). */
    byte[] read(String pathRelativo);

    /** Remove o arquivo do caminho relativo (no-op se não existir). */
    void delete(String pathRelativo);
}