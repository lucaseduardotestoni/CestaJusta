package org.furb.repositories;

import org.furb.model.MercadoComerciante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MercadoComercianteRepository extends JpaRepository<MercadoComerciante, Long> {

    boolean existsByMercadoIdAndComercianteId(Long mercadoId, Long comercianteId);

    Optional<MercadoComerciante> findByMercadoIdAndComercianteId(Long mercadoId, Long comercianteId);

    List<MercadoComerciante> findByMercadoId(Long mercadoId);

    List<MercadoComerciante> findByComercianteId(Long comercianteId);
}