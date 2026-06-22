export default function Sobre() {
  return (
    <section id="sobre" className="sec sobre">
      <div className="wrap sobre-grid">
        <div>
          <span className="eyebrow"><span className="dot"></span>Nosso propósito</span>
          <h2>Informação de preço não pode ser privilégio.</h2>
          <p className="lead">
            A CestaJusta nasceu de uma ideia simples: quando todo mundo enxerga os preços, ninguém paga a mais sozinho.
          </p>
          <p>
            Reunimos os preços que as pessoas registram no dia a dia e devolvemos isso de forma clara, transparente e gratuita — para que economizar seja um direito de qualquer comunidade, não só de quem tem tempo de pesquisar.
          </p>
        </div>
        <div className="stat-stack">
          <div className="stat-card">
            <span className="num green count" data-target="312" data-prefix="R$ ">R$ 312</span>
            <div className="desc">de economia média por família, todo mês</div>
          </div>
          <div className="stat-card">
            <span className="num orange count" data-target="1.4" data-decimals="1" data-suffix=" mi">1,4 mi</span>
            <div className="desc">de preços comparados em todo o país</div>
          </div>
          <div className="stat-card">
            <span className="num green count" data-target="100" data-suffix="%">100%</span>
            <div className="desc">gratuito, sem anúncios escondidos</div>
          </div>
        </div>
      </div>
    </section>
  )
}
