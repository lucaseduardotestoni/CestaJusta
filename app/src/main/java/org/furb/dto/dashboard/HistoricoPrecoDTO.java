package org.furb.dto.dashboard;

import java.util.List;

public class HistoricoPrecoDTO {

    private Long produtoId;
    private String produtoNome;
    private List<PontoSparklineDTO> pontos;

    public HistoricoPrecoDTO() {
    }

    public HistoricoPrecoDTO(Long produtoId, String produtoNome, List<PontoSparklineDTO> pontos) {
        this.produtoId = produtoId;
        this.produtoNome = produtoNome;
        this.pontos = pontos;
    }

    public Long getProdutoId() { return produtoId; }
    public String getProdutoNome() { return produtoNome; }
    public List<PontoSparklineDTO> getPontos() { return pontos; }
}