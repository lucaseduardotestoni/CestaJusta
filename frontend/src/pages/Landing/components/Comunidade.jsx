export default function Comunidade() {
  return (
    <section id="comunidade" className="sec">
      <div className="wrap">

        {/* Narrative header */}
        <div className="comm-narr">
          <span className="eyebrow">
            <span className="dot"></span>Comunidade
          </span>
          <p style={{ marginTop: '24px' }}>
            Sozinho, você pesquisa.{' '}
            <span className="accent">Junto</span>, a gente muda o preço da cidade inteira.
          </p>
        </div>

        {/* Stats grid */}
        <div className="comm-stats">
          <div className="cstat">
            <div className="n">
              <span className="count" data-target="126" data-suffix=" mil">126 mil</span>
            </div>
            <div className="l">pessoas economizando juntas</div>
          </div>

          <div className="cstat">
            <div className="n o">
              <span className="count" data-target="340">340</span>
            </div>
            <div className="l">cidades cobertas</div>
          </div>

          <div className="cstat">
            <div className="n">
              <span className="count" data-target="48" data-suffix=" mil">48 mil</span>
            </div>
            <div className="l">comparações nesta semana</div>
          </div>

          <div className="cstat">
            <div className="n o">
              <span className="count" data-target="4.9" data-decimals="1">4,9</span>
            </div>
            <div className="l">avaliação média do app</div>
          </div>
        </div>

      </div>
    </section>
  )
}
