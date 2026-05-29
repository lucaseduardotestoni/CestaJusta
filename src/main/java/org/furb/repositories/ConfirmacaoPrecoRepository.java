package org.furb.repositories;

import org.furb.model.ConfirmacaoPreco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfirmacaoPrecoRepository extends JpaRepository<ConfirmacaoPreco, Long> {
    long countByPrecoId(Long precoId);
    boolean existsByPrecoIdAndUsuarioId(Long precoId, Long usuarioId);
    void deleteByPrecoIdAndUsuarioId(Long precoId, Long usuarioId);
}