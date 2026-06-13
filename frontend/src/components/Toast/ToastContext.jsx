import { createContext, useCallback, useContext, useRef, useState } from 'react'
import Toast from './Toast'
import './Toast.css'

const ToastContext = createContext(null)

export function useToast() {
  const ctx = useContext(ToastContext)
  if (!ctx) throw new Error('useToast deve ser usado dentro de <ToastProvider>')
  return ctx
}

export function ToastProvider({ children }) {
  const [toasts, setToasts] = useState([])
  const proximoId = useRef(0)

  const remover = useCallback((id) => {
    setToasts(ts => ts.filter(t => t.id !== id))
  }, [])

  const mostrarToast = useCallback((mensagem, { tipo = 'sucesso', duracao = 5000 } = {}) => {
    const id = proximoId.current++
    setToasts(ts => [...ts, { id, mensagem, tipo, duracao }])
  }, [])

  return (
    <ToastContext.Provider value={{ mostrarToast }}>
      {children}
      <div className="toast-container" aria-live="polite" aria-atomic="true">
        {toasts.map(t => (
          <Toast key={t.id} mensagem={t.mensagem} tipo={t.tipo} duracao={t.duracao}
                 onFechar={() => remover(t.id)} />
        ))}
      </div>
    </ToastContext.Provider>
  )
}