import { useEffect, useState } from 'react'
import Modal from '../../components/Modal/Modal'
import { cadastrarMercado, editarMercado } from '../../services/api'
import { useToast } from '../../components/Toast/ToastContext'
import '../EnviarPreco/EnviarPrecoModal.css'

// Máscara determinística 00.000.000/0000-00 a partir dos dígitos.
function mascararCnpj(valor) {
  const d = (valor || '').replace(/\D/g, '').slice(0, 14)
  let r = ''
  for (let i = 0; i < d.length; i++) {
    if (i === 2 || i === 5) r += '.'
    else if (i === 8) r += '/'
    else if (i === 12) r += '-'
    r += d[i]
  }
  return r
}

export default function MercadoCadastroModal({ aberto, mercado, onFechar, onSalvo, onInativar, onAtivar }) {
  const { mostrarToast } = useToast()
  const edicao = !!mercado
  const [nomeFantasia, setNomeFantasia] = useState('')
  const [cnpj, setCnpj] = useState('')
  const [cidade, setCidade] = useState('')
  const [estado, setEstado] = useState('')
  const [erro, setErro] = useState(null)
  const [enviando, setEnviando] = useState(false)

  useEffect(() => {
    if (!aberto) return
    setNomeFantasia(mercado?.nomeFantasia || '')
    setCnpj(mascararCnpj(mercado?.cnpj || ''))
    setCidade(mercado?.cidade || '')
    setEstado(mercado?.estado || '')
    setErro(null)
    setEnviando(false)
  }, [aberto, mercado])

  async function salvar() {
    setErro(null)
    if (!nomeFantasia.trim()) { setErro('Informe o nome fantasia.'); return }
    if (cnpj.replace(/\D/g, '').length !== 14) { setErro('Informe um CNPJ com 14 dígitos.'); return }

    const dados = {
      nomeFantasia: nomeFantasia.trim(),
      cnpj: cnpj.trim(),
      cidade: cidade.trim() || undefined,
      estado: estado.trim() || undefined,
    }
    setEnviando(true)
    try {
      if (edicao) await editarMercado(mercado.id, dados)
      else await cadastrarMercado(dados)
      mostrarToast(edicao ? 'Mercado atualizado!' : 'Mercado cadastrado!')
      onSalvo()
    } catch (e) {
      setErro(e.message)
    } finally {
      setEnviando(false)
    }
  }

  return (
    <Modal aberto={aberto} onFechar={onFechar} titulo={edicao ? 'Editar mercado' : 'Cadastrar mercado'}>
      <div className="ep-form">
        <label htmlFor="mc-nome">Nome fantasia</label>
        <input id="mc-nome" type="text" value={nomeFantasia} disabled={enviando}
               onChange={e => setNomeFantasia(e.target.value)} maxLength={150} />

        <label htmlFor="mc-cnpj">CNPJ</label>
        <input id="mc-cnpj" type="text" value={cnpj} disabled={enviando}
               placeholder="00.000.000/0000-00" inputMode="numeric"
               onChange={e => setCnpj(mascararCnpj(e.target.value))} />

        <label htmlFor="mc-cidade">Cidade</label>
        <input id="mc-cidade" type="text" value={cidade} disabled={enviando}
               onChange={e => setCidade(e.target.value)} maxLength={150} />

        <label htmlFor="mc-estado">Estado (UF)</label>
        <input id="mc-estado" type="text" value={estado} disabled={enviando}
               placeholder="SC" maxLength={2}
               onChange={e => setEstado(e.target.value.replace(/[^a-zA-Z]/g, '').toUpperCase().slice(0, 2))} />

        {erro && <div className="ep-erro">{erro}</div>}

        <div className="ep-acoes">
          <button type="button" className="ep-btn-enviar" onClick={salvar} disabled={enviando}>
            {enviando ? 'Salvando…' : 'Salvar'}
          </button>
          {edicao && (mercado.ativo
            ? <button type="button" className="ep-btn-perigo" disabled={enviando}
                      onClick={() => onInativar?.(mercado)}>Inativar</button>
            : <button type="button" className="ep-btn-secundario" disabled={enviando}
                      onClick={() => onAtivar?.(mercado)}>Reativar</button>)}
          <button type="button" className="ep-btn-cancelar" onClick={onFechar} disabled={enviando}>
            Cancelar
          </button>
        </div>
      </div>
    </Modal>
  )
}
