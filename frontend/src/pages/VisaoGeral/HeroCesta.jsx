import './HeroCesta.css'

function brl(v) {
  if (v == null) return '—'
  return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(v)
}

export default function HeroCesta({ valorCesta, variacaoSemanal, economiaMedia }) {
  const varCor = variacaoSemanal == null
    ? null
    : variacaoSemanal < 0 ? 'var(--cor-success)'
    : variacaoSemanal > 0 ? 'var(--cor-danger)'
    : 'var(--cor-text-muted)'

  return (
    <section className="hero-cesta">
      <div className="hero-cesta-info">
        <span className="hero-cesta-titulo">Cesta básica (semana)</span>
        <span className="hero-cesta-valor">{brl(valorCesta)}</span>
        {variacaoSemanal != null && (
          <span className="hero-cesta-var" style={{ color: varCor }}>
            {variacaoSemanal > 0 ? '↑' : variacaoSemanal < 0 ? '↓' : ''} {Math.abs(variacaoSemanal).toFixed(1)}% em relação à semana passada
          </span>
        )}
        {economiaMedia != null && (
          <span className="hero-cesta-economia">Economia média: <b>{economiaMedia.toFixed(0)}%</b></span>
        )}
        <button type="button" className="hero-cesta-cta" disabled
                title="Em breve" aria-label="Ver mercados mais baratos (em breve)">
          Ver mercados mais baratos
        </button>
      </div>
      <img className="hero-cesta-img" src="/cesta-hero.webp" alt="" />
    </section>
  )
}
