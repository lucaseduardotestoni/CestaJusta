export default function Sobre() {
  return (
    <section id="sobre" className="sec sobre">
      <div className="wrap sobre-grid">
        <div>
          <span className="eyebrow"><span className="dot"></span>Nosso propósito</span>
          <h2>Informação de preço não pode ser privilégio.</h2>
          <p className="lead">
            A CestaJusta nasceu de uma ideia simples: centralizar os preços da cesta básica num único app, com informação clara e fácil de comparar, para que ninguém pague a mais sozinho.
          </p>
          <p>
            Reunimos os preços que as pessoas registram no dia a dia e devolvemos isso de forma clara, transparente e gratuita, para que economizar seja um direito de qualquer comunidade, não só de quem tem tempo de pesquisar.
          </p>
        </div>
        <div className="stat-stack">
          <div className="stat-card">
            <span className="num green count" data-target="148" data-prefix="R$ ">R$ 148</span>
            <div className="desc">de economia média por família, todo mês</div>
          </div>
          <div className="stat-card">
            <span className="num orange count" data-target="92" data-suffix=" mil">92 mil</span>
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
