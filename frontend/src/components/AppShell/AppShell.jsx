import { Navigate, Outlet } from 'react-router-dom'
import { useAuth } from '../../context/AuthContext'
import Sidebar from './Sidebar'
import Header from './Header'
import './AppShell.css'

export default function AppShell() {
  const { token } = useAuth()
  if (!token) return <Navigate to="/" replace />

  return (
    <div className="shell">
      <Sidebar />
      <div className="shell-main">
        <Header />
        <div className="shell-content">
          <Outlet />
        </div>
      </div>
    </div>
  )
}
