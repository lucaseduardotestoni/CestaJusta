import { useEffect, useRef, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../../context/AuthContext'

export default function Header({ onAbrirMenu, drawerAberto }) {
  const { usuario, logout } = useAuth()
  const navigate = useNavigate()
  const [aberto, setAberto] = useState(false)
  const wrapRef = useRef(null)

  const nome = usuario?.nome || 'Olá!'
  const email = usuario?.email || ''
  const inicial = (nome[0] || '?').toUpperCase()

  useEffect(() => {
    if (!aberto) return
    function aoClicarFora(e) {
      if (wrapRef.current && !wrapRef.current.contains(e.target)) setAberto(false)
    }
    function aoApertarEsc(e) {
      if (e.key === 'Escape') setAberto(false)
    }
    document.addEventListener('mousedown', aoClicarFora)
    document.addEventListener('keydown', aoApertarEsc)
    return () => {
      document.removeEventListener('mousedown', aoClicarFora)
      document.removeEventListener('keydown', aoApertarEsc)
    }
  }, [aberto])

  function handleLogout() {
    setAberto(false)
    logout()
    navigate('/')
  }

  return (
    <header className="header">
      <button
        type="button"
        className="header-hamburguer"
        aria-label="Abrir menu"
        aria-expanded={drawerAberto}
        onClick={onAbrirMenu}
      >
        ☰
      </button>
      <div className="header-actions">
        <button className="header-bell" type="button" aria-label="Notificações">🔔</button>

        <div className="header-user-wrap" ref={wrapRef}>
          <button
            type="button"
            className="header-user"
            aria-haspopup="menu"
            aria-expanded={aberto}
            onClick={() => setAberto(v => !v)}
          >
            <div className="header-avatar" aria-hidden="true">{inicial}</div>
            <span className="header-nome">{nome}</span>
            <span className="header-chevron" aria-hidden="true">▾</span>
          </button>

          {aberto && (
            <div className="header-menu" role="menu">
              <div className="header-menu-info">
                <div className="header-menu-nome">{nome}</div>
                {email && <div className="header-menu-email">{email}</div>}
              </div>
              <div className="header-menu-sep" />
              <button type="button" role="menuitem" className="header-menu-item" disabled>
                <span aria-hidden="true">👤</span>
                <span>Meu perfil</span>
              </button>
              <button type="button" role="menuitem" className="header-menu-item" disabled>
                <span aria-hidden="true">⚙️</span>
                <span>Configurações</span>
              </button>
              <div className="header-menu-sep" />
              <button
                type="button"
                role="menuitem"
                className="header-menu-item header-menu-sair"
                onClick={handleLogout}
              >
                <span>Sair</span>
              </button>
            </div>
          )}
        </div>
      </div>
    </header>
  )
}