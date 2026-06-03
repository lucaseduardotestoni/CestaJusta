package org.furb.services;

import org.furb.dto.denuncia.DenunciaCadastroDTO;
import org.furb.dto.denuncia.DenunciaResponseDTO;
import org.furb.enums.OrigemResolucao;
import org.furb.enums.StatusDenuncia;
import org.furb.enums.StatusPreco;
import org.furb.enums.TipoVoto;
import org.furb.model.Denuncia;
import org.furb.model.Preco;
import org.furb.model.Usuario;
import org.furb.model.VotoDenuncia;
import org.furb.repositories.DenunciaRepository;
import org.furb.repositories.MercadoComercianteRepository;
import org.furb.repositories.PrecoRepository;
import org.furb.repositories.VotoDenunciaRepository;
import org.furb.security.UsuarioAutenticadoProvider;
import org.furb.services.exeptions.BusinessException;
import org.furb.services.exeptions.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DenunciaService {

    private static final Logger log = LoggerFactory.getLogger(DenunciaService.class);
    private static final long VOTOS_PARA_RESOLVER = 3;
    private static final int DIAS_ANTISPAM = 3;
    private static final int DIAS_EXPIRACAO = 30;

    private final DenunciaRepository denunciaRepository;
    private final VotoDenunciaRepository votoDenunciaRepository;
    private final PrecoRepository precoRepository;
    private final MercadoComercianteRepository mercadoComercianteRepository;
    private final UsuarioAutenticadoProvider usuarioAutenticadoProvider;
    private final Clock clock;

    public DenunciaService(DenunciaRepository denunciaRepository,
                           VotoDenunciaRepository votoDenunciaRepository,
                           PrecoRepository precoRepository,
                           MercadoComercianteRepository mercadoComercianteRepository,
                           UsuarioAutenticadoProvider usuarioAutenticadoProvider,
                           Clock clock) {
        this.denunciaRepository = denunciaRepository;
        this.votoDenunciaRepository = votoDenunciaRepository;
        this.precoRepository = precoRepository;
        this.mercadoComercianteRepository = mercadoComercianteRepository;
        this.usuarioAutenticadoProvider = usuarioAutenticadoProvider;
        this.clock = clock;
    }

    @Transactional
    public Denuncia criar(DenunciaCadastroDTO dto) {
        Usuario usuario = usuarioAutenticadoProvider.getUsuarioAutenticado();
        Preco preco = precoRepository.findById(dto.getPrecoId())
                .orElseThrow(() -> new ResourceNotFoundException("Preço não encontrado."));

        LocalDateTime limiteAntiSpam = LocalDateTime.now(clock).minusDays(DIAS_ANTISPAM);
        if (denunciaRepository.existsByUsuarioIdAndPrecoIdAndDataCriacaoAfter(
                usuario.getId(), preco.getId(), limiteAntiSpam)) {
            throw new BusinessException("Você já denunciou este preço nos últimos 3 dias.");
        }

        Denuncia denuncia = new Denuncia();
        denuncia.setPreco(preco);
        denuncia.setUsuario(usuario);
        denuncia.setMotivo(dto.getMotivo());
        denuncia.setDescricao(dto.getDescricao());
        denuncia.setStatus(StatusDenuncia.PENDENTE);
        Denuncia salva = denunciaRepository.save(denuncia);
        log.info("Denúncia {} criada para o preço {} pelo usuário {}",
                salva.getId(), preco.getId(), usuario.getId());
        return salva;
    }

    @Transactional
    public void votar(Long denunciaId, TipoVoto tipo) {
        Usuario usuario = usuarioAutenticadoProvider.getUsuarioAutenticado();
        Denuncia denuncia = denunciaRepository.findById(denunciaId)
                .orElseThrow(() -> new ResourceNotFoundException("Denúncia não encontrada."));

        if (denuncia.getStatus() != StatusDenuncia.PENDENTE) {
            throw new BusinessException("Denúncia já resolvida não aceita votos.");
        }
        if (denuncia.getUsuario().getId().equals(usuario.getId())) {
            throw new BusinessException("O denunciante não pode votar na própria denúncia.");
        }
        Long mercadoId = denuncia.getPreco().getMercado().getId();
        if (mercadoComercianteRepository.existsByMercadoIdAndComercianteId(mercadoId, usuario.getId())) {
            throw new BusinessException("Donos do mercado não podem votar na denúncia do próprio preço.");
        }
        if (votoDenunciaRepository.existsByDenunciaIdAndUsuarioId(denunciaId, usuario.getId())) {
            throw new BusinessException("Você já votou nesta denúncia.");
        }

        VotoDenuncia voto = new VotoDenuncia();
        voto.setDenuncia(denuncia);
        voto.setUsuario(usuario);
        voto.setTipo(tipo);
        votoDenunciaRepository.save(voto);

        long mesmoTipo = votoDenunciaRepository.countByDenunciaIdAndTipo(denunciaId, tipo);
        if (mesmoTipo >= VOTOS_PARA_RESOLVER) {
            if (tipo == TipoVoto.CONFIRMA) {
                aprovar(denuncia, OrigemResolucao.SISTEMA);
            } else {
                rejeitar(denuncia, OrigemResolucao.SISTEMA);
            }
        }
    }

    @Transactional
    public void retirarVoto(Long denunciaId) {
        Usuario usuario = usuarioAutenticadoProvider.getUsuarioAutenticado();
        Denuncia denuncia = denunciaRepository.findById(denunciaId)
                .orElseThrow(() -> new ResourceNotFoundException("Denúncia não encontrada."));
        if (denuncia.getStatus() != StatusDenuncia.PENDENTE) {
            throw new BusinessException("Só é possível retirar voto de denúncia pendente.");
        }
        if (!votoDenunciaRepository.existsByDenunciaIdAndUsuarioId(denunciaId, usuario.getId())) {
            throw new ResourceNotFoundException("Você não votou nesta denúncia.");
        }
        votoDenunciaRepository.deleteByDenunciaIdAndUsuarioId(denunciaId, usuario.getId());
    }

    @Transactional
    public void cancelar(Long denunciaId) {
        Usuario usuario = usuarioAutenticadoProvider.getUsuarioAutenticado();
        Denuncia denuncia = denunciaRepository.findById(denunciaId)
                .orElseThrow(() -> new ResourceNotFoundException("Denúncia não encontrada."));
        if (!denuncia.getUsuario().getId().equals(usuario.getId())) {
            throw new BusinessException("Apenas o denunciante pode cancelar a denúncia.");
        }
        if (denuncia.getStatus() != StatusDenuncia.PENDENTE) {
            throw new BusinessException("Apenas denúncia pendente pode ser cancelada.");
        }
        denuncia.setStatus(StatusDenuncia.CANCELADA);
        denuncia.setDataResolucao(LocalDateTime.now(clock));
        denuncia.setResolvidoPor(OrigemResolucao.PROPRIO_DENUNCIANTE);
        denunciaRepository.save(denuncia);
        log.info("Denúncia {} CANCELADA pelo denunciante {}", denunciaId, usuario.getId());
    }

    @Transactional
    public void resolverComoAdmin(Long denunciaId, StatusDenuncia alvo) {
        if (alvo != StatusDenuncia.APROVADA && alvo != StatusDenuncia.REJEITADA) {
            throw new BusinessException("Override de ADMIN só aceita APROVADA ou REJEITADA.");
        }
        Denuncia denuncia = denunciaRepository.findById(denunciaId)
                .orElseThrow(() -> new ResourceNotFoundException("Denúncia não encontrada."));
        if (alvo == StatusDenuncia.APROVADA) {
            aprovar(denuncia, OrigemResolucao.ADMIN);
        } else {
            rejeitar(denuncia, OrigemResolucao.ADMIN);
        }
    }

    @Transactional
    public int expirarPendentes() {
        LocalDateTime limite = LocalDateTime.now(clock).minusDays(DIAS_EXPIRACAO);
        var expiradas = denunciaRepository.findByStatusAndDataCriacaoBefore(StatusDenuncia.PENDENTE, limite);
        for (Denuncia d : expiradas) {
            rejeitar(d, OrigemResolucao.SISTEMA);
        }
        if (!expiradas.isEmpty()) {
            log.info("Expiração automática: {} denúncia(s) sem engajamento rejeitada(s)", expiradas.size());
        }
        return expiradas.size();
    }

    @Transactional(readOnly = true)
    public DenunciaResponseDTO buscarPorId(Long id) {
        Denuncia d = denunciaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Denúncia não encontrada."));
        return toResponseDTO(d);
    }

    @Transactional(readOnly = true)
    public List<DenunciaResponseDTO> listarPorPreco(Long precoId) {
        return denunciaRepository.findByPrecoId(precoId).stream().map(this::toResponseDTO).toList();
    }

    private DenunciaResponseDTO toResponseDTO(Denuncia d) {
        long confirma = votoDenunciaRepository.countByDenunciaIdAndTipo(d.getId(), TipoVoto.CONFIRMA);
        long rejeita = votoDenunciaRepository.countByDenunciaIdAndTipo(d.getId(), TipoVoto.REJEITA);
        return new DenunciaResponseDTO(
                d.getId(), d.getPreco().getId(), d.getUsuario().getId(),
                d.getMotivo(), d.getDescricao(), d.getStatus(),
                confirma, rejeita, d.getDataCriacao(), d.getDataResolucao(), d.getResolvidoPor());
    }

    private void aprovar(Denuncia denuncia, OrigemResolucao origem) {
        denuncia.setStatus(StatusDenuncia.APROVADA);
        denuncia.setDataResolucao(LocalDateTime.now(clock));
        denuncia.setResolvidoPor(origem);
        Preco preco = denuncia.getPreco();
        preco.setStatus(StatusPreco.REJEITADO);
        precoRepository.save(preco);
        denunciaRepository.save(denuncia);
        log.info("Denúncia {} APROVADA ({}); preço {} marcado como REJEITADO",
                denuncia.getId(), origem, preco.getId());
    }

    private void rejeitar(Denuncia denuncia, OrigemResolucao origem) {
        denuncia.setStatus(StatusDenuncia.REJEITADA);
        denuncia.setDataResolucao(LocalDateTime.now(clock));
        denuncia.setResolvidoPor(origem);
        denunciaRepository.save(denuncia);
        log.info("Denúncia {} REJEITADA ({})", denuncia.getId(), origem);
    }
}