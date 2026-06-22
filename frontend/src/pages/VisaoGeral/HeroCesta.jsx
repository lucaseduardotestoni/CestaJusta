import './HeroCesta.css'
import ResumoSemana from './ResumoSemana'

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
    <div className="hero-wrap">
    <section className="hero-cesta">
      <span className="hc-shape" aria-hidden="true" />

      <div className="hc-top">
        <div className="hc-info">
          <span className="hc-titulo">Cesta básica (semana)</span>
          <span className="hc-valor">{brl(valorCesta)}</span>
          {variacaoSemanal != null && (
            <span className="hc-var" style={{ color: varCor }}>
              {variacaoSemanal > 0 ? '↑' : variacaoSemanal < 0 ? '↓' : ''} {Math.abs(variacaoSemanal).toFixed(1)}% em relação à semana passada
            </span>
          )}
        </div>

        <div className="hc-art">
          <img className="hc-img" src="/cesta-hero.webp" alt="" />
        </div>
      </div>

      <div className="hc-faixa">
        {economiaMedia != null && (
          <span className="hc-economia">Economia média: <b>{economiaMedia.toFixed(0)}%</b></span>
        )}
        <button type="button" className="hc-cta" disabled
                title="Em breve" aria-label="Ver mercados mais baratos (em breve)">
          <span className="hc-cta-full">Ver mercados mais baratos</span>
          <span className="hc-cta-short">Ver mercados</span>
        </button>
      </div>
    </section>

      <div className="hc-week"><ResumoSemana /></div>
    </div>
  )
}
