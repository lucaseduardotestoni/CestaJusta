import { Link } from 'react-router-dom'

export default function LandingFooter() {
  return (
    <footer className="landing-footer">
      <div className="wrap">
        <div className="foot-grid">
          <div className="foot-about">
            <a href="#top" className="brand">
              Cesta<b>Justa</b>
            </a>
            <p>Dados de preço abertos, feitos pela comunidade, para que economizar deixe de ser privilégio.</p>
          </div>

          <div className="foot-col">
            <h4>Plataforma</h4>
            <a href="#como">Como funciona</a>
            <a href="#produtos">Produtos</a>
            <a href="#comunidade">Comunidade</a>
          </div>

          <div className="foot-col">
            <h4>Proteção</h4>
            <a href="#denuncias">Denúncias</a>
            <a href="#sobre">Selo de confiança</a>
            <a href="#sobre">Transparência</a>
          </div>

          <div className="foot-col">
            <h4>Projeto</h4>
            <a href="#sobre">Sobre nós</a>
            <a href="#sobre">Privacidade</a>
            <a href="#sobre">Contato</a>
          </div>
        </div>

        <div className="foot-bottom">
          <span>© 2026 CestaJusta · Feito pela comunidade.</span>
          <span>Preço justo começa com informação.</span>
        </div>
      </div>
    </footer>
  )
}
