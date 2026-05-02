import { createContext, useContext, useState } from 'react'

const AuthContext = createContext(null)

function decodeJwt(token) {
  try {
    const payload = token.split('.')[1]
    const json = atob(payload.replace(/-/g, '+').replace(/_/g, '/'))
    return JSON.parse(json)
  } catch {
    return null
  }
}

function montarUsuarioDoToken(token) {
  const payload = decodeJwt(token)
  if (!payload) return null
  const email = payload.sub || ''
  const nome = email.includes('@') ? email.split('@')[0] : email
  return {
    nome,           // ex: "admin" extraído de "admin@cestajusta.com"
    email,
    tipo: null,     // backend ainda não envia esse claim; preencher quando /usuarios/me existir
  }
}

export function AuthProvider({ children }) {
  const [token, setToken] = useState(() => localStorage.getItem('token'))
  const [usuario, setUsuario] = useState(() => {
    const t = localStorage.getItem('token')
    return t ? montarUsuarioDoToken(t) : null
  })

  function saveToken(jwt) {
    localStorage.setItem('token', jwt)
    setToken(jwt)
    setUsuario(montarUsuarioDoToken(jwt))
  }

  function logout() {
    localStorage.removeItem('token')
    setToken(null)
    setUsuario(null)
  }

  return (
    <AuthContext.Provider value={{ token, usuario, saveToken, logout }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  return useContext(AuthContext)
}
