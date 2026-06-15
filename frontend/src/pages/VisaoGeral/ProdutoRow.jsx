import Sparkline from '../../components/Sparkline/Sparkline'
import { urlImagem } from '../../utils/urlImagem'

export default function ProdutoRow({ produto, onClick }) {
  const tendenciaClasse = produto.tendenciaPercentual > 0
    ? 'vg-tendencia-positiva'
    : produto.tendenciaPercentual < 0 ? 'vg-tendencia-negativa' : ''

  return (
    <tr onClick={() => onClick(produto)}>
      <td>
        <div className="vg-produto-info">
          {produto.imagemPath
            ? <img src={urlImagem(produto.imagemPath)} alt="" className="vg-produto-img"
                   onError={(e) => { e.currentTarget.style.visibility = 'hidden' }} />
            : <div className="vg-produto-img" />}
          <div>
            <div>{produto.nome}</div>
            <div className="vg-produto-meta">
              {produto.marca}{produto.unidadeMedida ? ` · ${produto.unidadeMedida}` : ''}
            </div>
          </div>
        </div>
      </td>
      <td><Sparkline pontos={produto.sparkline} /></td>
      <td>
        {formatBRL(produto.menorPreco)}
        <div className="vg-produto-meta">{produto.mercadoMenorNome}</div>
      </td>
      <td className={tendenciaClasse}>
        {produto.tendenciaPercentual > 0 ? '↑' : produto.tendenciaPercentual < 0 ? '↓' : '—'}
        {' '}{Math.abs(produto.tendenciaPercentual).toFixed(1)}%
      </td>
      <td><button type="button" className="vg-btn-detalhes">Ver detalhes</button></td>
    </tr>
  )
}

function formatBRL(v) {
  if (v == null) return '—'
  return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(v)
}
