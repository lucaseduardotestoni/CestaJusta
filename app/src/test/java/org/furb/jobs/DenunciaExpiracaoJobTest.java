package org.furb.jobs;

import org.furb.services.DenunciaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DenunciaExpiracaoJobTest {

    @Mock
    private DenunciaService denunciaService;

    @Test
    void expirarDenuncias_delegaParaService() {
        DenunciaExpiracaoJob job = new DenunciaExpiracaoJob(denunciaService);

        job.expirarDenuncias();

        verify(denunciaService).expirarPendentes();
    }
}