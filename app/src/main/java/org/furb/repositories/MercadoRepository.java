package org.furb.repositories;


import org.furb.model.Mercado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MercadoRepository extends JpaRepository<Mercado, Long> {
    Optional<Mercado> findByCnpj(String cnpj);
    boolean existsByCnpj(String cnpj);
    List<Mercado> findByNomeFantasiaContainingIgnoreCaseAndAtivoTrue(String nomeFantasia);
}