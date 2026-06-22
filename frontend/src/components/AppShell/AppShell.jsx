import { useState, useEffect } from 'react'
import { Navigate, Outlet, useLocation } from 'react-router-dom'
import { useAuth } from '../../context/AuthContext'
import { DashboardRefreshProvider } from '../../context/DashboardRefreshContext'
import Sidebar from './Sidebar'
import Header from './Header'
import './AppShell.css'

export default function AppShell() {
  const { status } = useAuth()
  const [drawerAberto, setDrawerAberto] = useState(false)
  const { pathname } = useLocation()

  // Fecha o drawer ao trocar de rota.
  useEffect(() => { setDrawerAberto(false) }, [pathname])

  // Fecha o drawer com Esc.
  useEffect(() => {
    if (!drawerAberto) return
    function onKey(e) { if (e.key === 'Escape') setDrawerAberto(false) }
    window.addEventListener('keydown', onKey)
    return () => window.removeEventListener('keydown', onKey)
  }, [drawerAberto])

  if (status === 'loading') {
    return <div className="shell-loading" role="status" aria-live="polite">Carregando…</div>
  }
  if (status === 'anonymous') return <Navigate to="/" replace />

  return (
    <DashboardRefreshProvider>
      <div className={`shell ${drawerAberto ? 'shell-drawer-aberto' : ''}`}>
        <Sidebar aberta={drawerAberto} onNavegar={() => setDrawerAberto(false)} />
        <div className="shell-backdrop" onClick={() => setDrawerAberto(false)} aria-hidden="true" />
        <div className="shell-main">
          <Header onAbrirMenu={() => setDrawerAberto(true)} drawerAberto={drawerAberto} />
          <div className="shell-content">
            <Outlet />
          </div>
        </div>
      </div>
    </DashboardRefreshProvider>
  )
}
