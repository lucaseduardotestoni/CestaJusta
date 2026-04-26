package org.furb.services;

import org.furb.dto.mercadoComerciante.MercadoComercianteResponseDTO;
import org.furb.enums.TipoUsuario;
import org.furb.model.Mercado;
import org.furb.model.MercadoComerciante;
import org.furb.model.Usuario;
import org.furb.repositories.MercadoComercianteRepository;
import org.furb.repositories.MercadoRepository;
import org.furb.repositories.UsuarioRepository;
import org.furb.services.exeptions.BusinessException;
import org.furb.services.exeptions.ResourceNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MercadoComercianteService {

    private final MercadoComercianteRepository repository;
    private final MercadoRepository mercadoRepository;
    private final UsuarioRepository usuarioRepository;

    public MercadoComercianteService(MercadoComercianteRepository repository,
                                     MercadoRepository mercadoRepository,
                                     UsuarioRepository usuarioRepository) {
        this.repository = repository;
        this.mercadoRepository = mercadoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public MercadoComercianteResponseDTO associar(Long mercadoId, Long comercianteId) {
        Usuario admin = getUsuarioAutenticado();

        Mercado mercado = mercadoRepository.findById(mercadoId)
                .orElseThrow(() -> new ResourceNotFoundException("Mercado não encontrado."));
        if (!Boolean.TRUE.equals(mercado.getAtivo())) {
            throw new BusinessException("Mercado inativo não aceita vinculação de comerciantes.");
        }

        Usuario comerciante = usuarioRepository.findById(comercianteId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));
        if (!Boolean.TRUE.equals(comerciante.getAtivo())) {
            throw new BusinessException("Usuário inativo não pode ser vinculado a um mercado.");
        }
        if (comerciante.getTipoUsuario() != TipoUsuario.COMERCIANTE) {
            throw new BusinessException("Apenas usuários do tipo COMERCIANTE podem ser vinculados a mercados.");
        }

        if (repository.existsByMercadoIdAndComercianteId(mercadoId, comercianteId)) {
            throw new BusinessException("Comerciante já está vinculado a este mercado.");
        }

        MercadoComerciante vinculo = new MercadoComerciante();
        vinculo.setMercado(mercado);
        vinculo.setComerciante(comerciante);
        vinculo.setVinculadoPor(admin);

        MercadoComerciante salvo = repository.save(vinculo);
        return toResponseDTO(salvo);
    }

    public void desassociar(Long mercadoId, Long comercianteId) {
        MercadoComerciante vinculo = repository.findByMercadoIdAndComercianteId(mercadoId, comercianteId)
                .orElseThrow(() -> new ResourceNotFoundException("Vínculo entre mercado e comerciante não encontrado."));
        repository.delete(vinculo);
    }

    public List<MercadoComercianteResponseDTO> listarDonosDoMercado(Long mercadoId) {
        if (!mercadoRepository.existsById(mercadoId)) {
            throw new ResourceNotFoundException("Mercado não encontrado.");
        }
        return repository.findByMercadoId(mercadoId)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public List<MercadoComercianteResponseDTO> listarMercadosDoComerciante(Long comercianteId) {
        if (!usuarioRepository.existsById(comercianteId)) {
            throw new ResourceNotFoundException("Usuário não encontrado.");
        }
        return repository.findByComercianteId(comercianteId)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public boolean ehDono(Long usuarioId, Long mercadoId) {
        return repository.existsByMercadoIdAndComercianteId(mercadoId, usuarioId);
    }

    private Usuario getUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new BusinessException("Usuário não autenticado.");
        }
        return usuarioRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário autenticado não encontrado."));
    }

    private MercadoComercianteResponseDTO toResponseDTO(MercadoComerciante v) {
        Mercado mercado = v.getMercado();
        Usuario comerciante = v.getComerciante();
        Usuario vinculadoPor = v.getVinculadoPor();
        return new MercadoComercianteResponseDTO(
                v.getId(),
                mercado.getId(),
                mercado.getNomeFantasia(),
                comerciante.getId(),
                comerciante.getNome(),
                comerciante.getEmail(),
                vinculadoPor.getId(),
                vinculadoPor.getNome(),
                v.getDataVinculacao()
        );
    }
}