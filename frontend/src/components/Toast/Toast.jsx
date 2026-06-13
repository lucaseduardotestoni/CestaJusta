import { useEffect, useState } from 'react'

// deve casar com a duração da transição de saída no Toast.css
const DURACAO_FADE = 350

export default function Toast({ mensagem, tipo = 'sucesso', duracao = 5000, onFechar }) {
  const [saindo, setSaindo] = useState(false)

  useEffect(() => {
    const tFade = setTimeout(() => setSaindo(true), Math.max(0, duracao - DURACAO_FADE))
    const tFechar = setTimeout(onFechar, duracao)
    return () => { clearTimeout(tFade); clearTimeout(tFechar) }
  }, [duracao, onFechar])

  return (
    <div className={`toast toast-${tipo}${saindo ? ' toast-saindo' : ''}`} role="status">
      <span className="toast-icone" aria-hidden="true">{tipo === 'erro' ? '⚠' : '✓'}</span>
      <span className="toast-msg">{mensagem}</span>
    </div>
  )
}
