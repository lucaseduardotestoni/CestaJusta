import { useAuth } from '../../context/AuthContext'

export default function Header() {
  const { usuario } = useAuth()
  const nome = usuario?.nome || 'Olá!'
  const inicial = (nome[0] || '?').toUpperCase()

  return (
    <header className="header">
      <div className="header-search">
        <input type="text" placeholder="Buscar..." aria-label="Buscar" />
      </div>
      <div className="header-actions">
        <button className="header-bell" type="button" aria-label="Notificações">🔔</button>
        <div className="header-user">
          <div className="header-avatar" aria-hidden="true">{inicial}</div>
          <span className="header-nome">{nome}</span>
        </div>
      </div>
    </header>
  )
}
