package org.furb.services;

import org.furb.dto.produto.ProdutoCadastroDTO;
import org.furb.dto.produto.ProdutoResponseDTO;
import org.furb.model.Categoria;
import org.furb.model.Produto;
import org.furb.services.exeptions.BusinessException;
import org.furb.services.exeptions.ResourceNotFoundException;
import org.furb.repositories.CategoriaRepository;
import org.furb.repositories.ProdutoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final CategoriaRepository categoriaRepository;

    public ProdutoService(ProdutoRepository produtoRepository,
                          CategoriaRepository categoriaRepository) {
        this.produtoRepository = produtoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    public ProdutoResponseDTO cadastrar(ProdutoCadastroDTO dto) {
        if (dto.getCodigoBarras() != null && !dto.getCodigoBarras().isBlank()) {
            if (produtoRepository.existsByCodigoBarras(dto.getCodigoBarras())) {
                throw new BusinessException("Já existe um produto cadastrado com este código de barras.");
            }
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

        Produto salvo = produtoRepository.save(produto);

        return toResponseDTO(salvo);
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
                produto.getImagemPath()
        );
    }
}