package org.furb.dto.denuncia;

import org.furb.enums.OrigemResolucao;
import org.furb.enums.StatusDenuncia;

import java.time.LocalDateTime;

public class DenunciaResponseDTO {

    private final Long id;
    private final Long precoId;
    private final Long denuncianteId;
    private final String motivo;
    private final String descricao;
    private final StatusDenuncia status;
    private final long votosConfirma;
    private final long votosRejeita;
    private final LocalDateTime dataCriacao;
    private final LocalDateTime dataResolucao;
    private final OrigemResolucao resolvidoPor;

    public DenunciaResponseDTO(Long id, Long precoId, Long denuncianteId, String motivo, String descricao,
                               StatusDenuncia status, long votosConfirma, long votosRejeita,
                               LocalDateTime dataCriacao, LocalDateTime dataResolucao, OrigemResolucao resolvidoPor) {
        this.id = id;
        this.precoId = precoId;
        this.denuncianteId = denuncianteId;
        this.motivo = motivo;
        this.descricao = descricao;
        this.status = status;
        this.votosConfirma = votosConfirma;
        this.votosRejeita = votosRejeita;
        this.dataCriacao = dataCriacao;
        this.dataResolucao = dataResolucao;
        this.resolvidoPor = resolvidoPor;
    }

    public Long getId() { return id; }
    public Long getPrecoId() { return precoId; }
    public Long getDenuncianteId() { return denuncianteId; }
    public String getMotivo() { return motivo; }
    public String getDescricao() { return descricao; }
    public StatusDenuncia getStatus() { return status; }
    public long getVotosConfirma() { return votosConfirma; }
    public long getVotosRejeita() { return votosRejeita; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public LocalDateTime getDataResolucao() { return dataResolucao; }
    public OrigemResolucao getResolvidoPor() { return resolvidoPor; }
}