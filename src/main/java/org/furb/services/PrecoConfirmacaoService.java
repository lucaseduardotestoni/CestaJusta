package org.furb.services;

import org.furb.enums.StatusPreco;
import org.furb.model.ConfirmacaoPreco;
import org.furb.model.Preco;
import org.furb.model.Usuario;
import org.furb.repositories.ConfirmacaoPrecoRepository;
import org.furb.repositories.PrecoRepository;
import org.furb.security.UsuarioAutenticadoProvider;
import org.furb.services.exeptions.BusinessException;
import org.furb.services.exeptions.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PrecoConfirmacaoService {

    private static final Logger log = LoggerFactory.getLogger(PrecoConfirmacaoService.class);
    private static final long CONFIRMACOES_PARA_CONFIRMAR = 3;

    private final PrecoRepository precoRepository;
    private final ConfirmacaoPrecoRepository confirmacaoPrecoRepository;
    private final UsuarioAutenticadoProvider usuarioAutenticadoProvider;

    public PrecoConfirmacaoService(PrecoRepository precoRepository,
                                   ConfirmacaoPrecoRepository confirmacaoPrecoRepository,
                                   UsuarioAutenticadoProvider usuarioAutenticadoProvider) {
        this.precoRepository = precoRepository;
        this.confirmacaoPrecoRepository = confirmacaoPrecoRepository;
        this.usuarioAutenticadoProvider = usuarioAutenticadoProvider;
    }

    @Transactional
    public void confirmar(Long precoId) {
        Usuario usuario = usuarioAutenticadoProvider.getUsuarioAutenticado();
        Preco preco = precoRepository.findById(precoId)
                .orElseThrow(() -> new ResourceNotFoundException("Preço não encontrado."));

        if (preco.getStatus() != StatusPreco.PENDENTE) {
            throw new BusinessException("Preço não está pendente de confirmação.");
        }
        if (preco.getUsuario().getId().equals(usuario.getId())) {
            throw new BusinessException("Não é possível confirmar o próprio preço.");
        }
        if (confirmacaoPrecoRepository.existsByPrecoIdAndUsuarioId(precoId, usuario.getId())) {
            throw new BusinessException("Você já confirmou este preço.");
        }

        ConfirmacaoPreco confirmacao = new ConfirmacaoPreco();
        confirmacao.setPreco(preco);
        confirmacao.setUsuario(usuario);
        confirmacaoPrecoRepository.save(confirmacao);

        long total = confirmacaoPrecoRepository.countByPrecoId(precoId);
        if (total >= CONFIRMACOES_PARA_CONFIRMAR) {
            preco.setStatus(StatusPreco.CONFIRMADO);
            precoRepository.save(preco);
            log.info("Preço {} CONFIRMADO após {} confirmações colaborativas", precoId, total);
        }
    }

    @Transactional
    public void retirarConfirmacao(Long precoId) {
        Usuario usuario = usuarioAutenticadoProvider.getUsuarioAutenticado();
        Preco preco = precoRepository.findById(precoId)
                .orElseThrow(() -> new ResourceNotFoundException("Preço não encontrado."));

        if (preco.getStatus() != StatusPreco.PENDENTE) {
            throw new BusinessException("Só é possível retirar confirmação de preço pendente.");
        }
        if (!confirmacaoPrecoRepository.existsByPrecoIdAndUsuarioId(precoId, usuario.getId())) {
            throw new ResourceNotFoundException("Você não confirmou este preço.");
        }
        confirmacaoPrecoRepository.deleteByPrecoIdAndUsuarioId(precoId, usuario.getId());
        log.info("Usuário {} retirou confirmação do preço {}", usuario.getId(), precoId);
    }
}