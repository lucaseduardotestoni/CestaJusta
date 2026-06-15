package org.furb.repositories;

import org.furb.enums.FotoStatus;
import org.furb.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    List<Produto> findByNomeContainingIgnoreCaseAndAtivoTrue(String nome);

    Optional<Produto> findByCodigoBarras(String codigoBarras);

    boolean existsByCodigoBarras(String codigoBarras);

    boolean existsByIdAndFotoStatusNot(Long id, FotoStatus fotoStatus);

    List<Produto> findByCategoriaIdAndAtivoTrue(Long categoriaId);
}