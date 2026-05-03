import './KpiCard.css'

export default function KpiCard({ titulo, valor, variacao, formato = 'numero' }) {
  const valorFormatado = formatar(valor, formato)
  const variacaoCor = variacao == null
    ? null
    : variacao < 0 ? 'var(--cor-success)' : variacao > 0 ? 'var(--cor-danger)' : null

  return (
    <div className="kpi-card">
      <span className="kpi-titulo">{titulo}</span>
      <span className="kpi-valor">{valorFormatado}</span>
      {variacao != null && (
        <span className="kpi-variacao" style={{ color: variacaoCor }}>
          {variacao > 0 ? '↑' : variacao < 0 ? '↓' : ''} {Math.abs(variacao).toFixed(1)}%
        </span>
      )}
    </div>
  )
}

function formatar(valor, formato) {
  if (valor == null) return '—'
  if (formato === 'brl') {
    return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(valor)
  }
  if (formato === 'percent') {
    return `${valor.toFixed(0)}%`
  }
  return new Intl.NumberFormat('pt-BR').format(valor)
}
