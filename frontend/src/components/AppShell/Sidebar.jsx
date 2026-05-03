import { NavLink } from 'react-router-dom'

const ITEMS = [
  { rota: '/dashboard',  rotulo: 'Visão geral', icone: '🏠' },
  { rota: '/comparar',   rotulo: 'Comparar',    icone: '⚖️',  desabilitado: true },
  { rota: '/mercados',   rotulo: 'Mercados',    icone: '🏪',  desabilitado: true },
  { rota: '/denuncias',  rotulo: 'Denúncias',   icone: '🚩',  desabilitado: true },
]

export default function Sidebar() {
  return (
    <aside className="sidebar">
      <div className="sidebar-logo">CestaJusta</div>

      <nav className="sidebar-nav">
        {ITEMS.map(item => (
          <NavLink
            key={item.rota}
            to={item.rota}
            className={({ isActive }) =>
              `sidebar-item ${isActive ? 'ativo' : ''} ${item.desabilitado ? 'desabilitado' : ''}`
            }
            onClick={(e) => { if (item.desabilitado) e.preventDefault() }}
          >
            <span aria-hidden="true">{item.icone}</span>
            <span>{item.rotulo}</span>
          </NavLink>
        ))}
      </nav>

      <button className="sidebar-cta" type="button" disabled>
        Enviar preço
      </button>
    </aside>
  )
}