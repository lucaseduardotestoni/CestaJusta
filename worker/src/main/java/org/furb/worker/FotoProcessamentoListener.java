package org.furb.worker;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.furb.enums.FotoStatus;
import org.furb.messaging.contract.FotoSolicitadaEvent;
import org.furb.messaging.contract.RoutingKeys;
import org.furb.model.Denuncia;
import org.furb.repositories.DenunciaRepository;
import org.furb.storage.FotoStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class FotoProcessamentoListener {

    private static final Logger log = LoggerFactory.getLogger(FotoProcessamentoListener.class);

    private final DenunciaRepository denunciaRepository;
    private final FotoStorage fotoStorage;
    private final FotoProcessor fotoProcessor;
    private final ObjectMapper objectMapper;

    public FotoProcessamentoListener(DenunciaRepository denunciaRepository,
                                     FotoStorage fotoStorage,
                                     FotoProcessor fotoProcessor,
                                     ObjectMapper objectMapper) {
        this.denunciaRepository = denunciaRepository;
        this.fotoStorage = fotoStorage;
        this.fotoProcessor = fotoProcessor;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = RoutingKeys.FILA_FOTO)
    @Transactional
    public void processar(String payloadJson) throws Exception {
        FotoSolicitadaEvent evento = objectMapper.readValue(payloadJson, FotoSolicitadaEvent.class);

        Denuncia denuncia = denunciaRepository.findById(evento.denunciaId()).orElse(null);
        if (denuncia == null) {
            log.warn("Denúncia {} não existe mais; descartando processamento de foto", evento.denunciaId());
            return;
        }
        // Idempotência: se já processou, não refaz (reentrega at-least-once)
        if (denuncia.getFotoStatus() == FotoStatus.PROCESSADO) {
            log.info("Foto da denúncia {} já processada; ignorando reentrega", denuncia.getId());
            return;
        }

        String original = evento.fotoPathOriginal();
        byte[] bytes = fotoStorage.read(original);

        byte[] sanitizada = fotoProcessor.sanitizar(bytes);
        byte[] thumb = fotoProcessor.thumbnail(bytes);

        String base = subpastaDe(original);
        String fotoPath = fotoStorage.store(base, sanitizada, "jpg");
        String thumbPath = fotoStorage.store(base, thumb, "jpg");

        // remove o original com EXIF/GPS
        fotoStorage.delete(original);

        denuncia.setFotoPath(fotoPath);
        denuncia.setThumbPath(thumbPath);
        denuncia.setFotoStatus(FotoStatus.PROCESSADO);
        denunciaRepository.save(denuncia);
        log.info("Foto da denúncia {} processada (sanitizada + thumbnail)", denuncia.getId());
    }

    private String subpastaDe(String pathRelativo) {
        int corte = pathRelativo.lastIndexOf('/');
        return corte > 0 ? pathRelativo.substring(0, corte) : "denuncias";
    }
}