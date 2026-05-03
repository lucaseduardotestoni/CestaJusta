import { useEffect, useState } from 'react'
import Modal from '../../components/Modal/Modal'
import Sparkline from '../../components/Sparkline/Sparkline'
import { getHistoricoProduto, getComparacaoProduto } from '../../services/api'

export default function ProdutoDetalheModal({ produto, onFechar }) {
  const [historico, setHistorico] = useState(null)
  const [comparacao, setComparacao] = useState(null)
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    if (!produto) return
    setLoading(true)
    Promise.all([
      getHistoricoProduto(produto.id),
      getComparacaoProduto(produto.id).catch(() => null),
    ]).then(([h, c]) => {
      setHistorico(h)
      setComparacao(c)
    }).finally(() => setLoading(false))
  }, [produto])

  return (
    <Modal aberto={!!produto} onFechar={onFechar}
           titulo={produto?.nome || 'Detalhes'}>
      {loading && <p>Carregando...</p>}

      {historico && (
        <section className="vg-modal-secao">
          <h3>Tendência de preço (30 dias)</h3>
          <Sparkline pontos={historico.pontos} largura={620} altura={180} />
        </section>
      )}

      {comparacao?.precosPorMercado?.length > 0 && (
        <section className="vg-modal-secao">
          <h3>Mercados ({comparacao.totalMercados})</h3>
          <ul className="vg-modal-mercados">
            {comparacao.precosPorMercado
              .slice()
              .sort((a, b) => a.valor - b.valor)
              .map(m => (
                <li key={m.mercadoId}>
                  <span>{m.mercadoNomeFantasia}</span>
                  <span>{new Intl.NumberFormat('pt-BR',
                    { style: 'currency', currency: 'BRL' }).format(m.valor)}</span>
                </li>
              ))}
          </ul>
        </section>
      )}
    </Modal>
  )
}
