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
            Cada denúncia vira um alerta para todo mundo — e pressão real contra quem cobra
            abusivo em momentos de necessidade.
          </p>

          <div className="den-stats">
            <div>
              <div className="n">
                <span className="count" data-target="9240">9.240</span>
              </div>
              <div className="l">denúncias registradas</div>
            </div>
            <div>
              <div className="n">
                <span className="count" data-target="87" data-suffix="%">87%</span>
              </div>
              <div className="l">resolvidas ou sinalizadas</div>
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
              <div className="t">Água 5L a R$ 18,90</div>
              <div className="d">Reportado por 14 pessoas · Zona Norte</div>
            </div>
            <span className="status status--sinalizado">Sinalizado</span>
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
              <div className="t">Botijão de gás +40% acima</div>
              <div className="d">Reportado por 31 pessoas · Centro</div>
            </div>
            <span className="status status--resolvido">Resolvido</span>
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
              <div className="t">Remédio sem tabela visível</div>
              <div className="d">Reportado por 8 pessoas · Bairro Velha</div>
            </div>
            <span className="status status--em-analise">Em análise</span>
          </div>

        </div>
      </div>
    </section>
  )
}
