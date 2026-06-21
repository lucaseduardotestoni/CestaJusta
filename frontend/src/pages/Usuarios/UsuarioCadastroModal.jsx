import { useEffect, useState } from 'react'
import Modal from '../../components/Modal/Modal'
import { criarUsuario } from '../../services/api'
import { useToast } from '../../components/Toast/ToastContext'
import { PAPEIS_CADASTRO } from './papeis'
import '../EnviarPreco/EnviarPrecoModal.css'

export default function UsuarioCadastroModal({ aberto, onFechar, onSalvo }) {
  const { mostrarToast } = useToast()
  const [nome, setNome] = useState('')
  const [email, setEmail] = useState('')
  const [senha, setSenha] = useState('')
  const [tipoUsuario, setTipoUsuario] = useState('CONSUMIDOR')
  const [erro, setErro] = useState(null)
  const [enviando, setEnviando] = useState(false)

  useEffect(() => {
    if (!aberto) return
    setNome('')
    setEmail('')
    setSenha('')
    setTipoUsuario('CONSUMIDOR')
    setErro(null)
    setEnviando(false)
  }, [aberto])

  async function salvar() {
    setErro(null)
    if (!nome.trim()) { setErro('Informe o nome.'); return }
    if (!email.trim()) { setErro('Informe o e-mail.'); return }
    if (senha.length < 6) { setErro('A senha deve ter no mínimo 6 caracteres.'); return }

    setEnviando(true)
    try {
      await criarUsuario({ nome: nome.trim(), email: email.trim(), senha, tipoUsuario })
      mostrarToast('Usuário cadastrado!')
      onSalvo()
    } catch (e) {
      setErro(e.message)
    } finally {
      setEnviando(false)
    }
  }

  return (
    <Modal aberto={aberto} onFechar={onFechar} titulo="Cadastrar usuário">
      <div className="ep-form">
        <label htmlFor="us-nome">Nome</label>
        <input id="us-nome" type="text" value={nome} disabled={enviando}
               onChange={e => setNome(e.target.value)} maxLength={150} />

        <label htmlFor="us-email">E-mail</label>
        <input id="us-email" type="email" value={email} disabled={enviando}
               onChange={e => setEmail(e.target.value)} maxLength={150} />

        <label htmlFor="us-senha">Senha</label>
        <input id="us-senha" type="password" value={senha} disabled={enviando}
               placeholder="Mínimo 6 caracteres" onChange={e => setSenha(e.target.value)} />

        <label htmlFor="us-tipo">Papel</label>
        <select id="us-tipo" value={tipoUsuario} disabled={enviando}
                onChange={e => setTipoUsuario(e.target.value)}>
          {PAPEIS_CADASTRO.map(p => <option key={p.value} value={p.value}>{p.label}</option>)}
        </select>

        {erro && <div className="ep-erro">{erro}</div>}

        <div className="ep-acoes">
          <button type="button" className="ep-btn-enviar" onClick={salvar} disabled={enviando}>
            {enviando ? 'Salvando…' : 'Salvar'}
          </button>
          <button type="button" className="ep-btn-cancelar" onClick={onFechar} disabled={enviando}>
            Cancelar
          </button>
        </div>
      </div>
    </Modal>
  )
}