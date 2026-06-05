package org.furb.jobs;

import org.furb.services.DenunciaService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DenunciaExpiracaoJob {

    private final DenunciaService denunciaService;

    public DenunciaExpiracaoJob(DenunciaService denunciaService) {
        this.denunciaService = denunciaService;
    }

    @Scheduled(cron = "0 0 3 * * *")
    public void expirarDenuncias() {
        denunciaService.expirarPendentes();
    }
}