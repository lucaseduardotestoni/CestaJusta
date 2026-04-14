package org.furb.repositories.model;

import org.furb.enums.StatusDenuncia;
import org.furb.model.Denuncia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface DenunciaRepository extends JpaRepository<Denuncia, Long> {
    List<Denuncia> findByStatus(StatusDenuncia status);
    List<Denuncia> findByUsuarioId(Long usuarioId);
}
