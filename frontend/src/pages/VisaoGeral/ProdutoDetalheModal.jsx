import { useEffect, useState } from 'react'
import Modal from '../../components/Modal/Modal'
import Sparkline from '../../components/Sparkline/Sparkline'
import { getHistoricoProduto } from '../../services/api'
import PrecosPorMercado from './PrecosPorMercado'

export default function ProdutoDetalheModal({ produto, onFechar }) {
  const [historico, setHistorico] = useState(null)
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    if (!produto) return
    setLoading(true)
    getHistoricoProduto(produto.id)
      .then(setHistorico)
      .finally(() => setLoading(false))
  }, [produto])

  return (
    <Modal aberto={!!produto} onFechar={onFechar}
           titulo={produto?.nome || 'Detalhes'}>
      {loading && <p>Carregando...</p>}

      {historico && (
        <section className="vg-modal-secao">
          <h3>Tendência de preço (30 dias)</h3>
          <Sparkline pontos={historico.pontos} largura={620} altura={180} comTooltip />
        </section>
      )}

      {produto && (
        <section className="vg-modal-secao">
          <h3>Preços por mercado</h3>
          <PrecosPorMercado produtoId={produto.id} />
        </section>
      )}
    </Modal>
  )
}
