export default function Denuncias() {
  return (
    <section id="denuncias" className="sec denuncias">
      <div className="wrap den-grid">

        {/* Left column — copy + stats + seal */}
        <div>
          <span className="eyebrow den-eyebrow">
            <span className="dot"></span>Proteção ao consumidor
          </span>
          <h2>Quando o preço passa do limite, a comunidade reage.</h2>
          <p className="sub">
            Cada denúncia vira um alerta para todo mundo e pressão real contra quem cobra
            abusivo em momentos de necessidade.
          </p>

          <div className="den-stats">
            <div>
              <div className="n">
                <span className="count" data-target="1380">1.380</span>
              </div>
              <div className="l">denúncias registradas</div>
            </div>
            <div>
              <div className="n">
                <span className="count" data-target="74" data-suffix="%">74%</span>
              </div>
              <div className="l">resolvidas pela comunidade</div>
            </div>
          </div>

          <div className="seal">
            <span className="s-ic">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"
                   strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
                <path d="M12 3 5 6v6c0 4.5 3 7.5 7 9 4-1.5 7-4.5 7-9V6l-7-3Z" />
                <path d="m9 12 2 2 4-4" />
              </svg>
            </span>
            <div>
              <b>Selo de confiança</b>
              <small>Preços verificados pela comunidade</small>
            </div>
          </div>
        </div>

        {/* Right column — complaint cards */}
        <div className="den-cards">

          {/* Card 1 — Sinalizado */}
          <div className="den">
            <span className="badge">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"
                   strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
                <path d="M12 9v4M12 17h.01M10.3 4l-7 12A2 2 0 0 0 5 19h14a2 2 0 0 0 1.7-3l-7-12a2 2 0 0 0-3.4 0Z" />
              </svg>
            </span>
            <div>
              <div className="t">Arroz 5kg a R$ 42,90</div>
              <div className="d">Reportado por 23 pessoas · Supermercado Koch</div>
            </div>
            <span className="status status--pendente">Pendente</span>
          </div>

          {/* Card 2 — Resolvido */}
          <div className="den">
            <span className="badge">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"
                   strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
                <path d="M12 9v4M12 17h.01M10.3 4l-7 12A2 2 0 0 0 5 19h14a2 2 0 0 0 1.7-3l-7-12a2 2 0 0 0-3.4 0Z" />
              </svg>
            </span>
            <div>
              <div className="t">Óleo de soja +55% em uma semana</div>
              <div className="d">Reportado por 18 pessoas · Giassi Supermercados</div>
            </div>
            <span className="status status--aprovada">Aprovada</span>
          </div>

          {/* Card 3 — Em análise */}
          <div className="den">
            <span className="badge">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"
                   strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
                <path d="M12 9v4M12 17h.01M10.3 4l-7 12A2 2 0 0 0 5 19h14a2 2 0 0 0 1.7-3l-7-12a2 2 0 0 0-3.4 0Z" />
              </svg>
            </span>
            <div>
              <div className="t">Leite 1L a R$ 4,79</div>
              <div className="d">Reportado por 4 pessoas · Top Supermercados</div>
            </div>
            <span className="status status--rejeitada">Rejeitada</span>
          </div>

        </div>
      </div>
    </section>
  )
}
