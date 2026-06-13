import { createContext, useCallback, useContext, useState } from 'react'

const DashboardRefreshContext = createContext(null)

export function useDashboardRefresh() {
  const ctx = useContext(DashboardRefreshContext)
  if (!ctx) throw new Error('useDashboardRefresh deve ser usado dentro de <DashboardRefreshProvider>')
  return ctx
}

export function DashboardRefreshProvider({ children }) {
  const [versao, setVersao] = useState(0)
  const solicitarRefresh = useCallback(() => setVersao(v => v + 1), [])
  return (
    <DashboardRefreshContext.Provider value={{ versao, solicitarRefresh }}>
      {children}
    </DashboardRefreshContext.Provider>
  )
}