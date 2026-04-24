package org.furb.services;

import org.furb.dto.preco.PrecoCadastroDTO;
import org.furb.dto.preco.PrecoResponseDTO;
import org.furb.enums.StatusPreco;
import org.furb.model.Mercado;
import org.furb.model.Preco;
import org.furb.model.Produto;
import org.furb.model.Usuario;
import org.furb.repositories.MercadoRepository;
import org.furb.repositories.PrecoRepository;
import org.furb.repositories.ProdutoRepository;
import org.furb.repositories.UsuarioRepository;
import org.furb.services.exeptions.BusinessException;
import org.furb.services.exeptions.ResourceNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PrecoService {

    private final PrecoRepository precoRepository;
    private final ProdutoRepository produtoRepository;
    private final MercadoRepository mercadoRepository;
    private final UsuarioRepository usuarioRepository;

    public PrecoService(PrecoRepository precoRepository,
                        ProdutoRepository produtoRepository,
                        MercadoRepository mercadoRepository,
                        UsuarioRepository usuarioRepository) {
        this.precoRepository = precoRepository;
        this.produtoRepository = produtoRepository;
        this.mercadoRepository = mercadoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public PrecoResponseDTO cadastrar(PrecoCadastroDTO dto) {
        Produto produto = produtoRepository.findById(dto.getProdutoId())
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado."));

        if (!produto.getAtivo()) {
            throw new BusinessException("Produto inativo não aceita registro de preço.");
        }

        Mercado mercado = mercadoRepository.findById(dto.getMercadoId())
                .orElseThrow(() -> new ResourceNotFoundException("Mercado não encontrado."));

        if (!mercado.getAtivo()) {
            throw new BusinessException("Mercado inativo não aceita registro de preço.");
        }

        Usuario usuario = getUsuarioAutenticado();

        Preco preco = new Preco();
        preco.setProduto(produto);
        preco.setMercado(mercado);
        preco.setUsuario(usuario);
        preco.setValor(dto.getValor());
        preco.setDataColeta(dto.getDataColeta());
        preco.setStatus(StatusPreco.PENDENTE);

        Preco salvo = precoRepository.save(preco);

        return toResponseDTO(salvo);
    }

    public PrecoResponseDTO buscarPorId(Long id) {
        Preco preco = precoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Preço não encontrado."));

        return toResponseDTO(preco);
    }

    public List<PrecoResponseDTO> listarPorProduto(Long produtoId) {
        if (!produtoRepository.existsById(produtoId)) {
            throw new ResourceNotFoundException("Produto não encontrado.");
        }

        return precoRepository.findByProdutoIdOrderByDataColetaDesc(produtoId)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public List<PrecoResponseDTO> listarPorMercado(Long mercadoId) {
        if (!mercadoRepository.existsById(mercadoId)) {
            throw new ResourceNotFoundException("Mercado não encontrado.");
        }

        return precoRepository.findByMercadoIdOrderByDataColetaDesc(mercadoId)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    private Usuario getUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getName() == null) {
            throw new BusinessException("Usuário não autenticado.");
        }

        String email = authentication.getName();
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário autenticado não encontrado."));
    }

    private PrecoResponseDTO toResponseDTO(Preco preco) {
        return new PrecoResponseDTO(
                preco.getId(),
                preco.getProduto().getId(),
                preco.getProduto().getNome(),
                preco.getMercado().getId(),
                preco.getMercado().getNomeFantasia(),
                preco.getUsuario().getId(),
                preco.getUsuario().getNome(),
                preco.getValor(),
                preco.getDataColeta(),
                preco.getStatus(),
                preco.getDataCriacao()
        );
    }
}