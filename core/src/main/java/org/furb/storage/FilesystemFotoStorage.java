package org.furb.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Component
public class FilesystemFotoStorage implements FotoStorage {

    private final Path raiz;

    public FilesystemFotoStorage(@Value("${app.uploads.dir:/data/uploads}") String uploadsDir) {
        this.raiz = Paths.get(uploadsDir).toAbsolutePath().normalize();
    }

    @Override
    public String store(String subpasta, byte[] conteudo, String extensao) {
        String nome = UUID.randomUUID() + "." + extensao;
        String pathRelativo = subpasta + "/" + nome;
        Path destino = resolver(pathRelativo);
        try {
            Files.createDirectories(destino.getParent());
            Files.write(destino, conteudo);
        } catch (IOException e) {
            throw new UncheckedIOException("Falha ao gravar foto em " + pathRelativo, e);
        }
        return pathRelativo;
    }

    @Override
    public byte[] read(String pathRelativo) {
        try {
            return Files.readAllBytes(resolver(pathRelativo));
        } catch (IOException e) {
            throw new UncheckedIOException("Falha ao ler foto " + pathRelativo, e);
        }
    }

    @Override
    public void delete(String pathRelativo) {
        try {
            Files.deleteIfExists(resolver(pathRelativo));
        } catch (IOException e) {
            throw new UncheckedIOException("Falha ao remover foto " + pathRelativo, e);
        }
    }

    /** Resolve e bloqueia path traversal (o caminho final precisa ficar dentro da raiz). */
    private Path resolver(String pathRelativo) {
        Path destino = raiz.resolve(pathRelativo).normalize();
        if (!destino.startsWith(raiz)) {
            throw new IllegalArgumentException("Caminho inválido: " + pathRelativo);
        }
        return destino;
    }
}