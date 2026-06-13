import { useState } from 'react'
import { confirmarPreco } from '../../services/api'
import { StatusDot, StatusBadge } from './StatusPrecoBadge'
import DenunciaInlineForm from './DenunciaInlineForm'

const moeda = new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' })

function formatarData(iso) {
  if (!iso) return ''
  const [ano, mes, dia] = iso.split('-')
  return `${dia}/${mes}/${ano}`
}

export default function PrecoLinha({ preco }) {
  const [confirmando, setConfirmando] = useState(false)
  const [confirmado, setConfirmado] = useState(false)
  const [erroConfirmar, setErroConfirmar] = useState(null)
  const [mostrarForm, setMostrarForm] = useState(false)
  const [denunciado, setDenunciado] = useState(false)

  const podeConfirmar = preco.status === 'PENDENTE' && !confirmado
  const podeDenunciar = preco.status !== 'REJEITADO' && !denunciado && !confirmado
  const rejeitado = preco.status === 'REJEITADO'

  async function confirmar() {
    setConfirmando(true)
    setErroConfirmar(null)
    try {
      await confirmarPreco(preco.id)
      setConfirmado(true)
    } catch (e) {
      setErroConfirmar(e.message)
    } finally {
      setConfirmando(false)
    }
  }

  return (
    <div className={`pm-linha${rejeitado ? ' pm-rejeitado' : ''}`}>
      <div className="pm-topo">
        <span className="pm-merc"><StatusDot status={preco.status} /> {preco.mercadoNomeFantasia}</span>
        <span><span className="pm-valor">{moeda.format(preco.valor)}</span> <StatusBadge status={preco.status} /></span>
      </div>
      <div className="pm-meta">
        Coletado em {formatarData(preco.dataColeta)}{preco.usuarioNome ? ` · por ${preco.usuarioNome}` : ''}
      </div>

      {!rejeitado && !mostrarForm && (
        <div className="pm-acoes">
          {podeConfirmar && (
            <button type="button" className="pm-btn pm-btn-conf" onClick={confirmar} disabled={confirmando}>
              {confirmando ? 'Confirmando…' : '✓ Confirmar'}
            </button>
          )}
          {podeDenunciar && (
            <button type="button" className="pm-btn pm-btn-den" onClick={() => setMostrarForm(true)}>
              ⚠ Denunciar
            </button>
          )}
        </div>
      )}

      {confirmado && <div className="pm-ok">✓ Você confirmou este preço</div>}
      {erroConfirmar && <div className="pm-erro">{erroConfirmar}</div>}
      {denunciado && <div className="pm-ok">✓ Denúncia enviada para análise</div>}

      {mostrarForm && (
        <DenunciaInlineForm
          preco={preco}
          onCancelar={() => setMostrarForm(false)}
          onSucesso={() => { setMostrarForm(false); setDenunciado(true) }}
        />
      )}
    </div>
  )
}