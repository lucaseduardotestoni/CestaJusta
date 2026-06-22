export default function ComoFunciona() {
  return (
    <section id="como" className="sec">
      <div className="wrap">
        <div className="sec-head">
          <span className="eyebrow"><span className="dot"></span>Como funciona</span>
          <h2>Três passos para pagar o preço certo</h2>
          <p>Rápido e sem complicação: a comunidade faz o trabalho pesado por você.</p>
        </div>
        <div className="timeline">
          <div className="step">
            <div className="step-num">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                <circle cx="11" cy="11" r="7"/>
                <path d="m21 21-4.3-4.3M8 11h6"/>
              </svg>
            </div>
            <span className="tag">01 · COMPARA</span>
            <h3>Compara preços perto de você</h3>
            <p>Busque um produto e veja onde está mais barato em tempo real, com dados da sua região.</p>
          </div>
          <div className="step">
            <div className="step-num">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                <path d="M4 12v8a1 1 0 0 0 1 1h14a1 1 0 0 0 1-1v-8"/>
                <path d="M12 3v13M8 7l4-4 4 4"/>
              </svg>
            </div>
            <span className="tag">02 · COMPARTILHA</span>
            <h3>Compartilha com a comunidade</h3>
            <p>Registrou um preço bom? Ele entra no mapa e ajuda milhares de pessoas a economizar.</p>
          </div>
          <div className="step">
            <div className="step-num">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                <path d="M4 21v-7M4 14l1.5-.4c2-.5 4 .9 6 .4s4-1.9 6-1.4l1 .3V5l-1-.3c-2-.5-4 .9-6 1.4s-4-.9-6-.4L4 6"/>
              </svg>
            </div>
            <span className="tag">03 · DENUNCIA</span>
            <h3>Denuncia preços abusivos</h3>
            <p>Encontrou um abuso? Reporte em segundos e some forças contra preços injustos.</p>
          </div>
        </div>
      </div>
    </section>
  )
}
