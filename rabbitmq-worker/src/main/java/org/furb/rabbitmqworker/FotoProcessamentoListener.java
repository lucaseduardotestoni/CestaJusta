package org.furb.rabbitmqworker;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.furb.enums.AlvoFoto;
import org.furb.messaging.contract.FotoSolicitadaEvent;
import org.furb.messaging.contract.RoutingKeys;
import org.furb.storage.FotoStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class FotoProcessamentoListener {

    private static final Logger log = LoggerFactory.getLogger(FotoProcessamentoListener.class);

    private final FotoStorage fotoStorage;
    private final FotoProcessor fotoProcessor;
    private final ObjectMapper objectMapper;
    private final Map<AlvoFoto, FotoAlvoHandler> handlers = new EnumMap<>(AlvoFoto.class);

    public FotoProcessamentoListener(FotoStorage fotoStorage,
                                     FotoProcessor fotoProcessor,
                                     ObjectMapper objectMapper,
                                     List<FotoAlvoHandler> handlerList) {
        this.fotoStorage = fotoStorage;
        this.fotoProcessor = fotoProcessor;
        this.objectMapper = objectMapper;
        for (FotoAlvoHandler h : handlerList) {
            handlers.put(h.tipo(), h);
        }
    }

    @RabbitListener(queues = RoutingKeys.FILA_FOTO)
    @Transactional
    public void processar(String payloadJson) throws Exception {
        FotoSolicitadaEvent evento = objectMapper.readValue(payloadJson, FotoSolicitadaEvent.class);

        FotoAlvoHandler handler = handlers.get(evento.tipo());
        if (handler == null) {
            log.warn("Sem handler para tipo {}; descartando", evento.tipo());
            return;
        }
        if (!handler.pendente(evento.alvoId())) {
            log.info("Alvo {} {} inexistente ou já processado; ignorando reentrega", evento.tipo(), evento.alvoId());
            return;
        }

        String original = evento.fotoPathOriginal();
        byte[] bytes = fotoStorage.read(original);

        byte[] sanitizada = fotoProcessor.sanitizar(bytes);
        byte[] thumb = fotoProcessor.thumbnail(bytes);

        String base = subpastaDe(original);
        String fotoPath = fotoStorage.store(base, sanitizada, "jpg");
        String thumbPath = fotoStorage.store(base, thumb, "jpg");

        fotoStorage.delete(original); // remove o original com EXIF/GPS

        handler.aplicar(evento.alvoId(), fotoPath, thumbPath);
        log.info("Foto do alvo {} {} processada (sanitizada + thumbnail)", evento.tipo(), evento.alvoId());
    }

    private String subpastaDe(String pathRelativo) {
        int corte = pathRelativo.lastIndexOf('/');
        return corte > 0 ? pathRelativo.substring(0, corte) : "outros";
    }
}