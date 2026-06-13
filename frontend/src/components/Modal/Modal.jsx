import { useEffect } from 'react'
import { createPortal } from 'react-dom'
import './Modal.css'

export default function Modal({ aberto, onFechar, children, titulo }) {
  useEffect(() => {
    if (!aberto) return
    function onKey(e) { if (e.key === 'Escape') onFechar() }
    window.addEventListener('keydown', onKey)
    document.body.style.overflow = 'hidden'
    return () => {
      window.removeEventListener('keydown', onKey)
      document.body.style.overflow = ''
    }
  }, [aberto, onFechar])

  if (!aberto) return null

  return createPortal(
    <div className="modal-backdrop" onClick={onFechar} role="presentation">
      <div className="modal-content" onClick={e => e.stopPropagation()} role="dialog" aria-modal="true">
        <div className="modal-header">
          <h2 className="modal-titulo">{titulo}</h2>
          <button className="modal-fechar" onClick={onFechar} aria-label="Fechar">×</button>
        </div>
        <div className="modal-body">{children}</div>
      </div>
    </div>,
    document.body
  )
}