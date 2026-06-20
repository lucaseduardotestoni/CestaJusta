import { useState } from 'react'
import { NavLink } from 'react-router-dom'
import EnviarPrecoModal from '../../pages/EnviarPreco/EnviarPrecoModal'

const ITEMS = [
  { rota: '/dashboard',  rotulo: 'Visão geral', icone: 'home' },
  { rota: '/produtos',   rotulo: 'Produtos',    icone: 'comparar' },
  { rota: '/mercados',   rotulo: 'Mercados',    icone: 'mercado',  desabilitado: true },
  { rota: '/denuncias',  rotulo: 'Denúncias',   icone: 'denuncia',
    subitens: [
      { rota: '/denuncias',        rotulo: 'Todas' },
      { rota: '/denuncias/minhas', rotulo: 'Minhas' },
    ] },
]

export default function Sidebar() {
  const [enviarAberto, setEnviarAberto] = useState(false)

  return (
    <aside className="sidebar">
      <div className="sidebar-logo">
        <img src="/logo.png" alt="CestaJusta" />
      </div>

      <nav className="sidebar-nav">
        {ITEMS.map(item => (
          <div key={item.rota}>
            <NavLink
              to={item.rota}
              end={item.rota === '/denuncias'}
              className={({ isActive }) =>
                `sidebar-item ${isActive ? 'ativo' : ''} ${item.desabilitado ? 'desabilitado' : ''}`
              }
              onClick={(e) => { if (item.desabilitado) e.preventDefault() }}
            >
              <span className={`sidebar-icone sidebar-icone-${item.icone}`} aria-hidden="true" />
              <span>{item.rotulo}</span>
            </NavLink>
            {item.subitens && (
              <div className="sidebar-subnav">
                {item.subitens.map(sub => (
                  <NavLink
                    key={sub.rota}
                    to={sub.rota}
                    end
                    className={({ isActive }) => `sidebar-subitem ${isActive ? 'ativo' : ''}`}
                  >
                    {sub.rotulo}
                  </NavLink>
                ))}
              </div>
            )}
          </div>
        ))}
      </nav>

      <button className="sidebar-cta" type="button" onClick={() => setEnviarAberto(true)}>
        Enviar preço
      </button>

      <EnviarPrecoModal aberto={enviarAberto} onFechar={() => setEnviarAberto(false)} />
    </aside>
  )
}