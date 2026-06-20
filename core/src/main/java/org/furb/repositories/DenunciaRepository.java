package org.furb.repositories;

import org.furb.enums.FotoStatus;
import org.furb.enums.StatusDenuncia;
import org.furb.model.Denuncia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DenunciaRepository extends JpaRepository<Denuncia, Long> {
    List<Denuncia> findByStatus(StatusDenuncia status);
    List<Denuncia> findByStatusOrderByDataCriacaoDesc(StatusDenuncia status);
    List<Denuncia> findByUsuarioId(Long usuarioId);
    List<Denuncia> findByUsuarioIdOrderByDataCriacaoDesc(Long usuarioId);
    List<Denuncia> findByPrecoId(Long precoId);
    boolean existsByUsuarioIdAndPrecoIdAndDataCriacaoAfter(Long usuarioId, Long precoId, LocalDateTime limite);
    boolean existsByIdAndFotoStatusNot(Long id, FotoStatus fotoStatus);
    List<Denuncia> findByStatusAndDataCriacaoBefore(StatusDenuncia status, LocalDateTime limite);

    @Query("select distinct d.preco.id from Denuncia d where d.usuario.id = :usuarioId and d.dataCriacao > :limite")
    List<Long> findPrecoIdsDenunciadosPeloUsuario(@Param("usuarioId") Long usuarioId, @Param("limite") LocalDateTime limite);
}
