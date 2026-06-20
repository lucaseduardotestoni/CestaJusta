package org.furb.dto.denuncia;

import org.furb.enums.FotoStatus;
import org.furb.enums.MotivoBloqueioVoto;
import org.furb.enums.OrigemResolucao;
import org.furb.enums.StatusDenuncia;
import org.furb.enums.TipoVoto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class DenunciaListItemDTO {

    private final Long id;
    private final Long precoId;
    private final Long denuncianteId;
    private final String produtoNome;
    private final String mercadoNome;
    private final BigDecimal precoValor;
    private final String motivo;
    private final String descricao;
    private final StatusDenuncia status;
    private final long votosConfirma;
    private final long votosRejeita;
    private final LocalDateTime dataCriacao;
    private final LocalDateTime dataResolucao;
    private final OrigemResolucao resolvidoPor;
    private final String fotoPath;
    private final String thumbPath;
    private final FotoStatus fotoStatus;
    private final TipoVoto meuVoto;
    private final boolean podeVotar;
    private final MotivoBloqueioVoto motivoBloqueio;

    public DenunciaListItemDTO(Long id, Long precoId, Long denuncianteId, String produtoNome, String mercadoNome,
                               BigDecimal precoValor, String motivo, String descricao, StatusDenuncia status,
                               long votosConfirma, long votosRejeita, LocalDateTime dataCriacao,
                               LocalDateTime dataResolucao, OrigemResolucao resolvidoPor, String fotoPath,
                               String thumbPath, FotoStatus fotoStatus, TipoVoto meuVoto, boolean podeVotar,
                               MotivoBloqueioVoto motivoBloqueio) {
        this.id = id;
        this.precoId = precoId;
        this.denuncianteId = denuncianteId;
        this.produtoNome = produtoNome;
        this.mercadoNome = mercadoNome;
        this.precoValor = precoValor;
        this.motivo = motivo;
        this.descricao = descricao;
        this.status = status;
        this.votosConfirma = votosConfirma;
        this.votosRejeita = votosRejeita;
        this.dataCriacao = dataCriacao;
        this.dataResolucao = dataResolucao;
        this.resolvidoPor = resolvidoPor;
        this.fotoPath = fotoPath;
        this.thumbPath = thumbPath;
        this.fotoStatus = fotoStatus;
        this.meuVoto = meuVoto;
        this.podeVotar = podeVotar;
        this.motivoBloqueio = motivoBloqueio;
    }

    public Long getId() { return id; }
    public Long getPrecoId() { return precoId; }
    public Long getDenuncianteId() { return denuncianteId; }
    public String getProdutoNome() { return produtoNome; }
    public String getMercadoNome() { return mercadoNome; }
    public BigDecimal getPrecoValor() { return precoValor; }
    public String getMotivo() { return motivo; }
    public String getDescricao() { return descricao; }
    public StatusDenuncia getStatus() { return status; }
    public long getVotosConfirma() { return votosConfirma; }
    public long getVotosRejeita() { return votosRejeita; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public LocalDateTime getDataResolucao() { return dataResolucao; }
    public OrigemResolucao getResolvidoPor() { return resolvidoPor; }
    public String getFotoPath() { return fotoPath; }
    public String getThumbPath() { return thumbPath; }
    public FotoStatus getFotoStatus() { return fotoStatus; }
    public TipoVoto getMeuVoto() { return meuVoto; }
    public boolean isPodeVotar() { return podeVotar; }
    public MotivoBloqueioVoto getMotivoBloqueio() { return motivoBloqueio; }
}
