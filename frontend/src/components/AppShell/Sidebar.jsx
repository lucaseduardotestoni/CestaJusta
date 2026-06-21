import { useState } from 'react'
import { NavLink, useLocation } from 'react-router-dom'
import { useAuth } from '../../context/AuthContext'
import EnviarPrecoModal from '../../pages/EnviarPreco/EnviarPrecoModal'

const ITEMS = [
  { rota: '/dashboard',  rotulo: 'Visão geral', icone: 'home' },
  { rota: '/produtos',   rotulo: 'Produtos',    icone: 'comparar' },
  { rota: '/mercados',   rotulo: 'Mercados',    icone: 'mercado' },
  { rota: '/denuncias',  rotulo: 'Denúncias',   icone: 'denuncia',
    subitens: [
      { rota: '/denuncias',        rotulo: 'Todas' },
      { rota: '/denuncias/minhas', rotulo: 'Minhas' },
    ] },
  { rota: '/usuarios',   rotulo: 'Usuários',    icone: 'usuarios', somenteAdmin: true },
]

export default function Sidebar({ aberta = false, onNavegar }) {
  const [enviarAberto, setEnviarAberto] = useState(false)
  const { usuario } = useAuth()
  const isAdmin = usuario?.tipo === 'ADMIN'
  const { pathname } = useLocation()
  // Submenu começa fechado; abre se a rota atual já for de um filho.
  const [expandido, setExpandido] = useState(
    () => ITEMS.find(i => i.subitens && pathname.startsWith(i.rota))?.rota ?? null,
  )

  return (
    <aside className={`sidebar ${aberta ? 'sidebar-aberta' : ''}`}>
      <div className="sidebar-logo">
        <img src="/logo.png" alt="CestaJusta" />
      </div>

      <nav className="sidebar-nav">
        {ITEMS.filter(item => !item.somenteAdmin || isAdmin).map(item => {
          if (item.subitens) {
            const aberto = expandido === item.rota
            const algumFilhoAtivo = item.subitens.some(s =>
              s.rota === item.rota ? pathname === s.rota : pathname.startsWith(s.rota))
            return (
              <div key={item.rota}>
                <button
                  type="button"
                  className={`sidebar-item sidebar-item-toggle ${algumFilhoAtivo ? 'ativo' : ''}`}
                  aria-expanded={aberto}
                  onClick={() => setExpandido(aberto ? null : item.rota)}
                >
                  <span className={`sidebar-icone sidebar-icone-${item.icone}`} aria-hidden="true" />
                  <span>{item.rotulo}</span>
                  <span className={`sidebar-chevron ${aberto ? 'aberto' : ''}`} aria-hidden="true">›</span>
                </button>
                {aberto && (
                  <div className="sidebar-subnav">
                    {item.subitens.map(sub => (
                      <NavLink
                        key={sub.rota}
                        to={sub.rota}
                        end
                        className={({ isActive }) => `sidebar-subitem ${isActive ? 'ativo' : ''}`}
                        onClick={() => onNavegar?.()}
                      >
                        {sub.rotulo}
                      </NavLink>
                    ))}
                  </div>
                )}
              </div>
            )
          }
          return (
            <NavLink
              key={item.rota}
              to={item.rota}
              className={({ isActive }) =>
                `sidebar-item ${isActive ? 'ativo' : ''} ${item.desabilitado ? 'desabilitado' : ''}`
              }
              onClick={(e) => {
                if (item.desabilitado) { e.preventDefault(); return }
                onNavegar?.()
              }}
            >
              <span className={`sidebar-icone sidebar-icone-${item.icone}`} aria-hidden="true" />
              <span>{item.rotulo}</span>
            </NavLink>
          )
        })}
      </nav>

      <button className="sidebar-cta" type="button" onClick={() => { setEnviarAberto(true); onNavegar?.() }}>
        Enviar preço
      </button>

      <EnviarPrecoModal aberto={enviarAberto} onFechar={() => setEnviarAberto(false)} />
    </aside>
  )
}