package org.furb.rabbitmqworker;

import org.furb.enums.AlvoFoto;
import org.furb.enums.FotoStatus;
import org.furb.model.Denuncia;
import org.furb.repositories.DenunciaRepository;
import org.springframework.stereotype.Component;

@Component
public class DenunciaFotoHandler implements FotoAlvoHandler {

    private final DenunciaRepository denunciaRepository;

    public DenunciaFotoHandler(DenunciaRepository denunciaRepository) {
        this.denunciaRepository = denunciaRepository;
    }

    @Override
    public AlvoFoto tipo() {
        return AlvoFoto.DENUNCIA;
    }

    @Override
    public boolean pendente(Long alvoId) {
        return denunciaRepository.existsByIdAndFotoStatusNot(alvoId, FotoStatus.PROCESSADO);
    }

    @Override
    public void aplicar(Long alvoId, String fotoPath, String thumbPath) {
        Denuncia d = denunciaRepository.findById(alvoId).orElseThrow();
        d.setFotoPath(fotoPath);
        d.setThumbPath(thumbPath);
        d.setFotoStatus(FotoStatus.PROCESSADO);
        denunciaRepository.save(d);
    }
}