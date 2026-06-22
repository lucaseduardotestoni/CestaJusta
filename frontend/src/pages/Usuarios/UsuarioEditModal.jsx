import { useEffect, useState } from 'react'
import Modal from '../../components/Modal/Modal'
import { atualizarUsuario } from '../../services/api'
import { useToast } from '../../components/Toast/ToastContext'
import { PAPEIS } from './papeis'
import '../EnviarPreco/EnviarPrecoModal.css'

export default function UsuarioEditModal({ aberto, usuario, onFechar, onSalvo, onInativar, onAtivar }) {
  const { mostrarToast } = useToast()
  const [nome, setNome] = useState('')
  const [email, setEmail] = useState('')
  const [tipoUsuario, setTipoUsuario] = useState('CONSUMIDOR')
  const [erro, setErro] = useState(null)
  const [enviando, setEnviando] = useState(false)

  useEffect(() => {
    if (!aberto || !usuario) return
    setNome(usuario.nome || '')
    setEmail(usuario.email || '')
    setTipoUsuario(usuario.tipoUsuario || 'CONSUMIDOR')
    setErro(null)
    setEnviando(false)
  }, [aberto, usuario])

  if (!aberto) return null

  async function salvar() {
    setErro(null)
    if (!nome.trim()) { setErro('Informe o nome.'); return }
    if (!email.trim()) { setErro('Informe o e-mail.'); return }

    setEnviando(true)
    try {
      await atualizarUsuario(usuario.id, { nome: nome.trim(), email: email.trim(), tipoUsuario })
      mostrarToast('Usuário atualizado!')
      onSalvo()
    } catch (e) {
      setErro(e.message)
    } finally {
      setEnviando(false)
    }
  }

  return (
    <Modal aberto={aberto} onFechar={onFechar} titulo="Editar usuário">
      <div className="ep-form">
        <label htmlFor="ue-nome">Nome</label>
        <input id="ue-nome" type="text" value={nome} disabled={enviando}
               onChange={e => setNome(e.target.value)} maxLength={150} />

        <label htmlFor="ue-email">E-mail</label>
        <input id="ue-email" type="email" value={email} disabled={enviando}
               onChange={e => setEmail(e.target.value)} maxLength={150} />

        <label htmlFor="ue-tipo">Papel</label>
        <select id="ue-tipo" value={tipoUsuario} disabled={enviando}
                onChange={e => setTipoUsuario(e.target.value)}>
          {PAPEIS.map(p => <option key={p.value} value={p.value}>{p.label}</option>)}
        </select>

        {erro && <div className="ep-erro">{erro}</div>}

        <div className="ep-acoes">
          <button type="button" className="ep-btn-enviar" onClick={salvar} disabled={enviando}>
            {enviando ? 'Salvando…' : 'Salvar'}
          </button>
          {usuario?.ativo
            ? <button type="button" className="ep-btn-perigo" disabled={enviando}
                      onClick={() => onInativar?.(usuario)}>Inativar</button>
            : <button type="button" className="ep-btn-secundario" disabled={enviando}
                      onClick={() => onAtivar?.(usuario)}>Reativar</button>}
          <button type="button" className="ep-btn-cancelar" onClick={onFechar} disabled={enviando}>
            Cancelar
          </button>
        </div>
      </div>
    </Modal>
  )
}
