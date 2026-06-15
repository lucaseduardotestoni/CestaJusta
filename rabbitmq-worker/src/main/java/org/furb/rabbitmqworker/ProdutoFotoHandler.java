package org.furb.rabbitmqworker;

import org.furb.enums.AlvoFoto;
import org.furb.enums.FotoStatus;
import org.furb.model.Produto;
import org.furb.repositories.ProdutoRepository;
import org.springframework.stereotype.Component;

@Component
public class ProdutoFotoHandler implements FotoAlvoHandler {

    private final ProdutoRepository produtoRepository;

    public ProdutoFotoHandler(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    @Override
    public AlvoFoto tipo() {
        return AlvoFoto.PRODUTO;
    }

    @Override
    public boolean pendente(Long alvoId) {
        return produtoRepository.existsByIdAndFotoStatusNot(alvoId, FotoStatus.PROCESSADO);
    }

    @Override
    public void aplicar(Long alvoId, String fotoPath, String thumbPath) {
        Produto p = produtoRepository.findById(alvoId).orElseThrow();
        p.setImagemPath(fotoPath);
        p.setThumbPath(thumbPath);
        p.setFotoStatus(FotoStatus.PROCESSADO);
        produtoRepository.save(p);
    }
}