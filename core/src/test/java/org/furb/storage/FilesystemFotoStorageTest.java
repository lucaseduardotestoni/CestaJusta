package org.furb.storage;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FilesystemFotoStorageTest {

    @TempDir
    Path raiz;

    private FilesystemFotoStorage storage() {
        return new FilesystemFotoStorage(raiz.toString());
    }

    @Test
    void store_gravaArquivo_eRetornaPathRelativoComUuidEExtensao() {
        byte[] conteudo = "abc".getBytes(StandardCharsets.UTF_8);

        String pathRelativo = storage().store("denuncias/2026/06", conteudo, "jpg");

        assertThat(pathRelativo).startsWith("denuncias/2026/06/").endsWith(".jpg");
        assertThat(raiz.resolve(pathRelativo)).exists();
    }

    @Test
    void store_eRead_fazemRoundTrip() {
        byte[] conteudo = new byte[]{1, 2, 3, 4, 5};
        FilesystemFotoStorage storage = storage();

        String path = storage.store("denuncias", conteudo, "png");

        assertThat(storage.read(path)).containsExactly(conteudo);
    }

    @Test
    void store_criaSubpastasInexistentes() {
        String path = storage().store("a/b/c/d", new byte[]{9}, "jpg");
        assertThat(raiz.resolve(path)).exists();
    }

    @Test
    void delete_removeArquivoExistente() {
        FilesystemFotoStorage storage = storage();
        String path = storage.store("denuncias", new byte[]{1}, "jpg");
        assertThat(raiz.resolve(path)).exists();

        storage.delete(path);

        assertThat(raiz.resolve(path)).doesNotExist();
    }

    @Test
    void delete_arquivoInexistente_naoLanca() {
        storage().delete("denuncias/nao-existe.jpg");
    }

    @Test
    void read_arquivoInexistente_lancaUncheckedIOException() {
        assertThatThrownBy(() -> storage().read("denuncias/nao-existe.jpg"))
                .isInstanceOf(UncheckedIOException.class);
    }

    @Test
    void store_pathTraversal_eBloqueado() {
        assertThatThrownBy(() -> storage().store("../../etc", new byte[]{1}, "jpg"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("inválido");
    }

    @Test
    void read_pathTraversal_eBloqueado() {
        assertThatThrownBy(() -> storage().read("../../../etc/passwd"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}