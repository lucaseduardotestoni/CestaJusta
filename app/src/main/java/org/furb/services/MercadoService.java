package org.furb.services;

import org.furb.dto.mercado.MercadoCadastroDTO;
import org.furb.dto.mercado.MercadoResponseDTO;
import org.furb.model.Mercado;
import org.furb.repositories.MercadoRepository;
import org.furb.services.exeptions.BusinessException;
import org.furb.services.exeptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MercadoService {

    private final MercadoRepository mercadoRepository;

    public MercadoService(MercadoRepository mercadoRepository) {
        this.mercadoRepository = mercadoRepository;
    }

    public MercadoResponseDTO cadastrar(MercadoCadastroDTO dto) {
        if (mercadoRepository.existsByCnpj(dto.getCnpj())) {
            throw new BusinessException("Já existe um mercado cadastrado com este CNPJ.");
        }

        Mercado mercado = new Mercado();
        mercado.setNomeFantasia(dto.getNomeFantasia());
        mercado.setCnpj(dto.getCnpj());
        mercado.setCidade(dto.getCidade());
        mercado.setEstado(dto.getEstado());
        mercado.setAtivo(true);

        Mercado salvo = mercadoRepository.save(mercado);

        return toResponseDTO(salvo);
    }

    public List<MercadoResponseDTO> listarAtivos() {
        return mercadoRepository.findAll()
                .stream()
                .filter(Mercado::getAtivo)
                .map(this::toResponseDTO)
                .toList();
    }

    public List<MercadoResponseDTO> listarTodos() {
        return mercadoRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public MercadoResponseDTO editar(Long id, MercadoCadastroDTO dto) {
        Mercado mercado = mercadoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mercado não encontrado."));

        mercadoRepository.findByCnpj(dto.getCnpj())
                .filter(outro -> !outro.getId().equals(id))
                .ifPresent(outro -> {
                    throw new BusinessException("Já existe um mercado cadastrado com este CNPJ.");
                });

        mercado.setNomeFantasia(dto.getNomeFantasia());
        mercado.setCnpj(dto.getCnpj());
        mercado.setCidade(dto.getCidade());
        mercado.setEstado(dto.getEstado());

        Mercado atualizado = mercadoRepository.save(mercado);
        return toResponseDTO(atualizado);
    }

    public MercadoResponseDTO buscarPorId(Long id) {
        Mercado mercado = mercadoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mercado não encontrado."));

        return toResponseDTO(mercado);
    }

    public List<MercadoResponseDTO> buscarPorNome(String nome) {
        return mercadoRepository.findByNomeFantasiaContainingIgnoreCaseAndAtivoTrue(nome)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public MercadoResponseDTO inativar(Long id) {
        Mercado mercado = mercadoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mercado não encontrado."));

        if (!mercado.getAtivo()) {
            throw new BusinessException("Mercado já está inativo.");
        }

        mercado.setAtivo(false);
        Mercado atualizado = mercadoRepository.save(mercado);

        return toResponseDTO(atualizado);
    }

    public MercadoResponseDTO ativar(Long id) {
        Mercado mercado = mercadoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mercado não encontrado."));

        if (mercado.getAtivo()) {
            throw new BusinessException("Mercado já está ativo.");
        }

        mercado.setAtivo(true);
        Mercado atualizado = mercadoRepository.save(mercado);

        return toResponseDTO(atualizado);
    }

    private MercadoResponseDTO toResponseDTO(Mercado mercado) {
        return new MercadoResponseDTO(
                mercado.getId(),
                mercado.getNomeFantasia(),
                mercado.getCnpj(),
                mercado.getCidade(),
                mercado.getEstado(),
                mercado.getAtivo()
        );
    }
}