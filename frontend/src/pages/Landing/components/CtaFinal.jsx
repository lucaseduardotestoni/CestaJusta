import { Link } from 'react-router-dom'

export default function CtaFinal() {
  return (
    <section id="cta" className="cta-final">
      <div className="wrap">
        <div className="cta-box">
          <div className="cta-glow" />
          <svg className="cta-leaf a" viewBox="0 0 24 24" fill="currentColor">
            <path d="M21 3c-9 0-15 4-15 12 0 1.5.3 3 .8 4.2C4 18 3 15 3 12 3 12 3 21 6 21c6 0 15-3 15-18Z" />
          </svg>
          <svg className="cta-leaf b" viewBox="0 0 24 24" fill="currentColor">
            <path d="M21 3c-9 0-15 4-15 12 0 1.5.3 3 .8 4.2C4 18 3 15 3 12 3 12 3 21 6 21c6 0 15-3 15-18Z" />
          </svg>
          <h2>Faça parte de uma comunidade que combate preços abusivos.</h2>
          <p>É grátis, leva menos de um minuto e começa a economizar hoje.</p>
          <Link to="/register" className="btn btn-primary btn-lg">Criar conta gratuita</Link>
        </div>
      </div>
    </section>
  )
}
