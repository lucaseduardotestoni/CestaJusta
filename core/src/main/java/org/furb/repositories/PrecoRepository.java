package org.furb.repositories;

import org.furb.enums.StatusPreco;
import org.furb.model.Preco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Repository
public interface PrecoRepository extends JpaRepository<Preco, Long> {
    List<Preco> findByProdutoId(Long produtoId);
    List<Preco> findByMercadoId(Long mercadoId);
    List<Preco> findByProdutoIdAndMercadoId(Long produtoId, Long mercadoId);
    List<Preco> findByProdutoIdOrderByDataColetaDesc(Long produtoId);
    List<Preco> findByMercadoIdOrderByDataColetaDesc(Long mercadoId);
    List<Preco> findByDataColetaBetweenAndStatusIn(LocalDate inicio, LocalDate fim, Collection<StatusPreco> status);
    List<Preco> findByProdutoIdAndDataColetaBetweenAndStatusIn(Long produtoId, LocalDate inicio, LocalDate fim, Collection<StatusPreco> status);
}