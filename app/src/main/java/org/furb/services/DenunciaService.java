package org.furb.services;

import org.furb.dto.denuncia.DenunciaCadastroDTO;
import org.furb.dto.denuncia.DenunciaListItemDTO;
import org.furb.dto.denuncia.DenunciaResponseDTO;
import org.furb.enums.AlvoFoto;
import org.furb.enums.FotoStatus;
import org.furb.enums.MotivoBloqueioVoto;
import org.furb.enums.OrigemResolucao;
import org.furb.enums.StatusDenuncia;
import org.furb.enums.StatusPreco;
import org.furb.enums.TipoVoto;
import org.furb.messaging.contract.FotoSolicitadaEvent;
import org.furb.messaging.contract.RoutingKeys;
import org.furb.outbox.OutboxService;
import org.furb.storage.FotoStorage;
import org.springframework.web.multipart.MultipartFile;
import org.furb.model.Denuncia;
import org.furb.model.Preco;
import org.furb.model.Produto;
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
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
public class DenunciaService {

    private static final Logger log = LoggerFactory.getLogger(DenunciaService.class);
    private static final long VOTOS_PARA_RESOLVER = 3;
    private static final int DIAS_ANTISPAM = 3;
    private static final int DIAS_EXPIRACAO = 30;
    private static final Set<String> MIME_PERMITIDOS = Set.of("image/jpeg", "image/png", "image/webp");

    private final DenunciaRepository denunciaRepository;
    private final VotoDenunciaRepository votoDenunciaRepository;
    private final PrecoRepository precoRepository;
    private final MercadoComercianteRepository mercadoComercianteRepository;
    private final UsuarioAutenticadoProvider usuarioAutenticadoProvider;
    private final Clock clock;
    private final FotoStorage fotoStorage;
    private final OutboxService outboxService;

    public DenunciaService(DenunciaRepository denunciaRepository,
                           VotoDenunciaRepository votoDenunciaRepository,
                           PrecoRepository precoRepository,
                           MercadoComercianteRepository mercadoComercianteRepository,
                           UsuarioAutenticadoProvider usuarioAutenticadoProvider,
                           Clock clock,
                           FotoStorage fotoStorage,
                           OutboxService outboxService) {
        this.denunciaRepository = denunciaRepository;
        this.votoDenunciaRepository = votoDenunciaRepository;
        this.precoRepository = precoRepository;
        this.mercadoComercianteRepository = mercadoComercianteRepository;
        this.usuarioAutenticadoProvider = usuarioAutenticadoProvider;
        this.clock = clock;
        this.fotoStorage = fotoStorage;
        this.outboxService = outboxService;
    }

    @Transactional
    public Denuncia criar(DenunciaCadastroDTO dto, MultipartFile foto) {
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

        boolean temFoto = foto != null && !foto.isEmpty();
        if (temFoto) {
            String fotoPath = armazenarFoto(foto);
            denuncia.setFotoPath(fotoPath);
            denuncia.setFotoStatus(FotoStatus.PROCESSANDO);
        } else {
            denuncia.setFotoStatus(FotoStatus.SEM_FOTO);
        }

        Denuncia salva = denunciaRepository.save(denuncia);
        log.info("Denúncia {} criada para o preço {} pelo usuário {} (foto: {})",
                salva.getId(), preco.getId(), usuario.getId(), temFoto);

        if (temFoto) {
            String eventoId = outboxService.novoEventoId();
            outboxService.registrar(eventoId, RoutingKeys.FOTO_SOLICITADA,
                    new FotoSolicitadaEvent(eventoId, AlvoFoto.DENUNCIA, salva.getId(), salva.getFotoPath()));
        }
        return salva;
    }

    private String armazenarFoto(MultipartFile foto) {
        String mime = foto.getContentType();
        if (mime == null || !MIME_PERMITIDOS.contains(mime)) {
            throw new BusinessException("Formato de imagem não suportado. Use JPG, PNG ou WEBP.");
        }
        String extensao = switch (mime) {
            case "image/png" -> "png";
            case "image/webp" -> "webp";
            default -> "jpg";
        };
        LocalDate hoje = LocalDate.now(clock);
        String subpasta = String.format("denuncias/%d/%02d", hoje.getYear(), hoje.getMonthValue());
        try {
            return fotoStorage.store(subpasta, foto.getBytes(), extensao);
        } catch (java.io.IOException e) {
            throw new BusinessException("Falha ao ler a imagem enviada.");
        }
    }

    @Transactional
    public void votar(Long denunciaId, TipoVoto tipo) {
        Usuario usuario = usuarioAutenticadoProvider.getUsuarioAutenticado();
        Denuncia denuncia = denunciaRepository.findById(denunciaId)
                .orElseThrow(() -> new ResourceNotFoundException("Denúncia não encontrada."));

        MotivoBloqueioVoto bloqueio = calcularBloqueio(denuncia, usuario);
        if (bloqueio != null) {
            throw new BusinessException(switch (bloqueio) {
                case JA_RESOLVIDA -> "Denúncia já resolvida não aceita votos.";
                case DENUNCIANTE -> "O denunciante não pode votar na própria denúncia.";
                case DONO_MERCADO -> "Donos do mercado não podem votar na denúncia do próprio preço.";
            });
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

    @Transactional(readOnly = true)
    public List<DenunciaListItemDTO> listarMinhas() {
        Usuario usuario = usuarioAutenticadoProvider.getUsuarioAutenticado();
        return denunciaRepository.findByUsuarioIdOrderByDataCriacaoDesc(usuario.getId()).stream()
                .map(d -> toListItemDTO(d, usuario)).toList();
    }

    @Transactional(readOnly = true)
    public List<DenunciaListItemDTO> listarTodas(StatusDenuncia status) {
        Usuario usuario = usuarioAutenticadoProvider.getUsuarioAutenticado();
        List<Denuncia> denuncias = (status == null)
                ? denunciaRepository.findAll(Sort.by(Sort.Direction.DESC, "dataCriacao"))
                : denunciaRepository.findByStatusOrderByDataCriacaoDesc(status);
        return denuncias.stream().map(d -> toListItemDTO(d, usuario)).toList();
    }

    /** Ids dos preços que o usuário autenticado já denunciou dentro da janela anti-spam. */
    @Transactional(readOnly = true)
    public List<Long> precosDenunciadosPeloUsuarioNaJanela() {
        Usuario usuario = usuarioAutenticadoProvider.getUsuarioAutenticado();
        LocalDateTime limite = LocalDateTime.now(clock).minusDays(DIAS_ANTISPAM);
        return denunciaRepository.findPrecoIdsDenunciadosPeloUsuario(usuario.getId(), limite);
    }

    private DenunciaResponseDTO toResponseDTO(Denuncia d) {
        long confirma = votoDenunciaRepository.countByDenunciaIdAndTipo(d.getId(), TipoVoto.CONFIRMA);
        long rejeita = votoDenunciaRepository.countByDenunciaIdAndTipo(d.getId(), TipoVoto.REJEITA);
        return new DenunciaResponseDTO(
                d.getId(), d.getPreco().getId(), d.getUsuario().getId(),
                d.getMotivo(), d.getDescricao(), d.getStatus(),
                confirma, rejeita, d.getDataCriacao(), d.getDataResolucao(), d.getResolvidoPor(),
                d.getFotoPath(), d.getThumbPath(), d.getFotoStatus());
    }

    private MotivoBloqueioVoto calcularBloqueio(Denuncia d, Usuario atual) {
        if (d.getStatus() != StatusDenuncia.PENDENTE) return MotivoBloqueioVoto.JA_RESOLVIDA;
        if (d.getUsuario().getId().equals(atual.getId())) return MotivoBloqueioVoto.DENUNCIANTE;
        Long mercadoId = d.getPreco().getMercado().getId();
        if (mercadoComercianteRepository.existsByMercadoIdAndComercianteId(mercadoId, atual.getId())) {
            return MotivoBloqueioVoto.DONO_MERCADO;
        }
        return null;
    }

    private DenunciaListItemDTO toListItemDTO(Denuncia d, Usuario atual) {
        long confirma = votoDenunciaRepository.countByDenunciaIdAndTipo(d.getId(), TipoVoto.CONFIRMA);
        long rejeita = votoDenunciaRepository.countByDenunciaIdAndTipo(d.getId(), TipoVoto.REJEITA);
        Preco preco = d.getPreco();
        Produto produto = preco.getProduto();
        MotivoBloqueioVoto bloqueio = calcularBloqueio(d, atual);
        TipoVoto meuVoto = votoDenunciaRepository.findByDenunciaIdAndUsuarioId(d.getId(), atual.getId())
                .map(VotoDenuncia::getTipo).orElse(null);
        return new DenunciaListItemDTO(
                d.getId(), preco.getId(), d.getUsuario().getId(),
                produto != null ? produto.getNome() : null,
                preco.getMercado() != null ? preco.getMercado().getNomeFantasia() : null,
                preco.getValor(),
                d.getMotivo(), d.getDescricao(), d.getStatus(),
                confirma, rejeita, d.getDataCriacao(), d.getDataResolucao(), d.getResolvidoPor(),
                d.getFotoPath(), d.getThumbPath(), d.getFotoStatus(),
                meuVoto, bloqueio == null, bloqueio);
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
