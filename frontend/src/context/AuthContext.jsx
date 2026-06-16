import { createContext, useContext, useEffect, useState, useCallback } from 'react'
import { getMe, logout as apiLogout, setOnUnauthorized } from '../services/api'

const AuthContext = createContext(null)

function mapMe(me) {
  if (!me) return null
  return {
    nome: me.nome,
    email: me.email,
    tipo: me.tipoUsuario || null, // ADMIN | CONSUMIDOR | COMERCIANTE
  }
}

export function AuthProvider({ children }) {
  const [usuario, setUsuario] = useState(null)
  const [status, setStatus] = useState('loading') // 'loading' | 'authenticated' | 'anonymous'

  const clearSession = useCallback(() => {
    setUsuario(null)
    setStatus('anonymous')
  }, [])

  // Rebusca /usuarios/me e atualiza o estado. Retorna true se autenticado.
  const refresh = useCallback(async () => {
    try {
      const me = await getMe()
      setUsuario(mapMe(me))
      setStatus('authenticated')
      return true
    } catch {
      clearSession()
      return false
    }
  }, [clearSession])

  // 401 sem recuperação (refresh falhou) → derruba a sessão no client.
  useEffect(() => {
    setOnUnauthorized(() => clearSession())
    return () => setOnUnauthorized(null)
  }, [clearSession])

  // Bootstrap no carregamento da app.
  useEffect(() => {
    refresh()
  }, [refresh])

  const logout = useCallback(async () => {
    try {
      await apiLogout()
    } finally {
      clearSession()
    }
  }, [clearSession])

  return (
    <AuthContext.Provider value={{ usuario, status, refresh, logout }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  return useContext(AuthContext)
}
