import { Link } from 'react-router-dom'
import './NotFound.css'

export default function NotFoundPage() {
  return (
    <div className="nf-wrap">
      <img className="nf-img" src="/not-found.png" alt="" width="240" height="240" />
      <h1 className="nf-titulo">Página não encontrada</h1>
      <p className="nf-texto">
        A página que você tentou acessar não existe ou foi movida.
      </p>
      <Link to="/dashboard" className="nf-botao">Voltar para a visão geral</Link>
    </div>
  )
}