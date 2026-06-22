import { Link } from 'react-router-dom'

export default function Hero() {
  return (
    <section className="hero">
      <div className="wrap hero-grid">
        <div className="hero-text">
          <span className="eyebrow">
            <span className="dot"></span>
            Preços da sua região
          </span>
          <h1>
            Preço justo começa com <span className="accent">informação</span>.
          </h1>
          <p className="sub">
            Compare preços, descubra abusos e economize com dados reais da sua comunidade.
          </p>
          <div className="hero-cta">
            <Link to="/register" className="btn btn-primary btn-lg">
              Criar conta gratuita
            </Link>
            <a href="#como" className="btn btn-ghost btn-lg">
              Ver como funciona
            </a>
          </div>
          <div className="hero-trust">
            <div className="avatars">
              <span style={{ background: '#0E7C42' }}>A</span>
              <span style={{ background: '#EF6E35' }}>M</span>
              <span style={{ background: '#13934F' }}>J</span>
              <span style={{ background: '#D85A30' }}>L</span>
            </div>
            <span>+48 mil comparações feitas esta semana</span>
          </div>
        </div>

        <div className="hero-visual">
          <div className="glow"></div>
          <img
            className="basket-img"
            src="/cesta-landing.png"
            alt="Cesta básica com produtos e preços"
          />
        </div>
      </div>
    </section>
  )
}
