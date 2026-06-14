package org.furb.services;

import org.furb.dto.produto.ProdutoCadastroDTO;
import org.furb.dto.produto.ProdutoResponseDTO;
import org.furb.enums.AlvoFoto;
import org.furb.enums.FotoStatus;
import org.furb.messaging.contract.FotoSolicitadaEvent;
import org.furb.messaging.contract.RoutingKeys;
import org.furb.model.Categoria;
import org.furb.model.Produto;
import org.furb.outbox.OutboxService;
import org.furb.services.exeptions.BusinessException;
import org.furb.services.exeptions.ResourceNotFoundException;
import org.furb.repositories.CategoriaRepository;
import org.furb.repositories.ProdutoRepository;
import org.furb.storage.FotoStorage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

@Service
public class ProdutoService {

    private static final Set<String> MIME_PERMITIDOS = Set.of("image/jpeg", "image/png");
    private static final long TAMANHO_MAX = 5L * 1024 * 1024;

    private final ProdutoRepository produtoRepository;
    private final CategoriaRepository categoriaRepository;
    private final FotoStorage fotoStorage;
    private final OutboxService outboxService;

    public ProdutoService(ProdutoRepository produtoRepository,
                          CategoriaRepository categoriaRepository,
                          FotoStorage fotoStorage,
                          OutboxService outboxService) {
        this.produtoRepository = produtoRepository;
        this.categoriaRepository = categoriaRepository;
        this.fotoStorage = fotoStorage;
        this.outboxService = outboxService;
    }

    @Transactional
    public ProdutoResponseDTO cadastrar(ProdutoCadastroDTO dto, MultipartFile foto) {
        if (dto.getCodigoBarras() != null && !dto.getCodigoBarras().isBlank()
                && produtoRepository.existsByCodigoBarras(dto.getCodigoBarras())) {
            throw new BusinessException("Já existe um produto cadastrado com este código de barras.");
        }

        Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada."));

        Produto produto = new Produto();
        produto.setNome(dto.getNome());
        produto.setCodigoBarras(dto.getCodigoBarras());
        produto.setMarca(dto.getMarca());
        produto.setUnidadeMedida(dto.getUnidadeMedida());
        produto.setCategoria(categoria);
        produto.setAtivo(true);
        aplicarFotoSeHouver(produto, foto);

        Produto salvo = produtoRepository.save(produto);
        emitirEventoSeFoto(salvo);
        return toResponseDTO(salvo);
    }

    @Transactional
    public ProdutoResponseDTO editar(Long id, ProdutoCadastroDTO dto, MultipartFile foto) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado."));

        if (dto.getCodigoBarras() != null && !dto.getCodigoBarras().isBlank()) {
            produtoRepository.findByCodigoBarras(dto.getCodigoBarras())
                    .filter(outro -> !outro.getId().equals(id))
                    .ifPresent(outro -> {
                        throw new BusinessException("Já existe um produto com este código de barras.");
                    });
        }

        Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada."));

        produto.setNome(dto.getNome());
        produto.setCodigoBarras(dto.getCodigoBarras());
        produto.setMarca(dto.getMarca());
        produto.setUnidadeMedida(dto.getUnidadeMedida());
        produto.setCategoria(categoria);

        boolean trocouFoto = foto != null && !foto.isEmpty();
        if (trocouFoto) {
            aplicarFotoSeHouver(produto, foto);
        }

        Produto salvo = produtoRepository.save(produto);
        if (trocouFoto) {
            emitirEventoSeFoto(salvo);
        }
        return toResponseDTO(salvo);
    }

    private void aplicarFotoSeHouver(Produto produto, MultipartFile foto) {
        if (foto == null || foto.isEmpty()) {
            if (produto.getImagemPath() == null) {
                produto.setFotoStatus(FotoStatus.SEM_FOTO);
            }
            return;
        }
        String mime = foto.getContentType();
        if (mime == null || !MIME_PERMITIDOS.contains(mime)) {
            throw new BusinessException("Formato de imagem não suportado. Use JPG ou PNG.");
        }
        if (foto.getSize() > TAMANHO_MAX) {
            throw new BusinessException("Imagem excede o tamanho máximo de 5MB.");
        }
        String extensao = "image/png".equals(mime) ? "png" : "jpg";
        try {
            String path = fotoStorage.store("produtos", foto.getBytes(), extensao);
            produto.setImagemPath(path);
            produto.setFotoStatus(FotoStatus.PROCESSANDO);
        } catch (java.io.IOException e) {
            throw new BusinessException("Falha ao ler a imagem enviada.");
        }
    }

    private void emitirEventoSeFoto(Produto salvo) {
        if (salvo.getFotoStatus() == FotoStatus.PROCESSANDO) {
            String eventoId = outboxService.novoEventoId();
            outboxService.registrar(eventoId, RoutingKeys.FOTO_SOLICITADA,
                    new FotoSolicitadaEvent(eventoId, AlvoFoto.PRODUTO, salvo.getId(), salvo.getImagemPath()));
        }
    }

    public List<ProdutoResponseDTO> listarAtivos() {
        return produtoRepository.findAll()
                .stream()
                .filter(Produto::getAtivo)
                .map(this::toResponseDTO)
                .toList();
    }

    public List<ProdutoResponseDTO> listarTodos() {
        return produtoRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public ProdutoResponseDTO buscarPorId(Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado."));

        return toResponseDTO(produto);
    }

    public List<ProdutoResponseDTO> buscarPorNome(String nome) {
        return produtoRepository.findByNomeContainingIgnoreCaseAndAtivoTrue(nome)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public List<ProdutoResponseDTO> buscarPorCategoria(Long categoriaId) {
        if (!categoriaRepository.existsById(categoriaId)) {
            throw new ResourceNotFoundException("Categoria não encontrada.");
        }
        return produtoRepository.findByCategoriaIdAndAtivoTrue(categoriaId)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public ProdutoResponseDTO inativar(Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado."));

        if (!produto.getAtivo()) {
            throw new BusinessException("Produto já está inativo.");
        }

        produto.setAtivo(false);
        Produto atualizado = produtoRepository.save(produto);

        return toResponseDTO(atualizado);
    }

    public ProdutoResponseDTO ativar(Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado."));

        if (produto.getAtivo()) {
            throw new BusinessException("Produto já está ativo.");
        }

        produto.setAtivo(true);
        Produto atualizado = produtoRepository.save(produto);

        return toResponseDTO(atualizado);
    }

    private ProdutoResponseDTO toResponseDTO(Produto produto) {
        return new ProdutoResponseDTO(
                produto.getId(),
                produto.getNome(),
                produto.getCodigoBarras(),
                produto.getMarca(),
                produto.getUnidadeMedida(),
                produto.getCategoria() != null ? produto.getCategoria().getNome() : null,
                produto.getAtivo(),
                produto.getImagemPath(),
                produto.getThumbPath()
        );
    }
}