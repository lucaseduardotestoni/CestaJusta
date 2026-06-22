import { Link } from 'react-router-dom'

export default function LandingNav() {
  return (
    <nav className="landing-nav">
      <div className="wrap nav-inner">
        <a href="#top" className="brand" aria-label="CestaJusta - início">
          <img src="/logo.png" alt="CestaJusta" className="brand-logo" />
        </a>

        <div className="nav-links">
          <a href="#como">Como funciona</a>
          <a href="#sobre">Sobre</a>
          <a href="#produtos">Produtos</a>
          <a href="#denuncias">Denúncias</a>
          <a href="#comunidade">Comunidade</a>
        </div>

        <div className="nav-cta">
          <Link to="/login" className="nav-entrar">Entrar</Link>
          <Link to="/register" className="btn btn-primary">Criar conta gratuita</Link>
        </div>
      </div>
    </nav>
  )
}
