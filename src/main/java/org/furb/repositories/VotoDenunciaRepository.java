package org.furb.repositories;

import org.furb.enums.TipoVoto;
import org.furb.model.VotoDenuncia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface VotoDenunciaRepository extends JpaRepository<VotoDenuncia, Long> {
    long countByDenunciaIdAndTipo(Long denunciaId, TipoVoto tipo);
    boolean existsByDenunciaIdAndUsuarioId(Long denunciaId, Long usuarioId);

    @Transactional
    void deleteByDenunciaIdAndUsuarioId(Long denunciaId, Long usuarioId);
}