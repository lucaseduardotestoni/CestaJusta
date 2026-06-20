import { useState } from 'react'
import Modal from '../../components/Modal/Modal'
import { useAuth } from '../../context/AuthContext'
import { useToast } from '../../components/Toast/ToastContext'
import { urlImagem } from '../../utils/urlImagem'
import {
  votarDenuncia, retirarVotoDenuncia, cancelarDenuncia, resolverDenuncia,
} from '../../services/api'

const BLOQUEIO_MSG = {
  DENUNCIANTE: 'Você criou esta denúncia.',
  DONO_MERCADO: 'Você é dono deste mercado.',
  JA_RESOLVIDA: 'Denúncia já resolvida.',
}

function brl(v) {
  if (v == null) return '—'
  return Number(v).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })
}

function descricaoResolucao(d) {
  if (d.status === 'CANCELADA') return 'Cancelada pelo próprio autor'
  const verbo = d.status === 'APROVADA' ? 'Aprovada' : 'Rejeitada'
  if (d.resolvidoPor === 'ADMIN') return `${verbo} por um administrador`
  if (d.resolvidoPor === 'SISTEMA') return `${verbo} pela comunidade`
  return verbo
}

export default function DenunciaDetalheModal({ denuncia, onFechar, onMudou }) {
  const { usuario } = useAuth()
  const { mostrarToast } = useToast()
  const [processando, setProcessando] = useState(false)

  if (!denuncia) return null

  const isAdmin = usuario?.tipo === 'ADMIN'
  // O /me não expõe id; numa denúncia PENDENTE minha o backend marca DENUNCIANTE
  // (calcularBloqueio checa status antes), e o cancelar só aparece quando pendente.
  const souDenunciante = denuncia.motivoBloqueio === 'DENUNCIANTE'
  const pendente = denuncia.status === 'PENDENTE'
  const tooltip = denuncia.motivoBloqueio ? BLOQUEIO_MSG[denuncia.motivoBloqueio] : ''

  async function acao(fn, msgOk) {
    setProcessando(true)
    try {
      await fn()
      mostrarToast(msgOk)
      onMudou()
    } catch (e) {
      mostrarToast(e.message, { tipo: 'erro' })
    } finally {
      setProcessando(false)
    }
  }

  const jaVotei = !!denuncia.meuVoto
  const votar = (tipo) => acao(() => votarDenuncia(denuncia.id, tipo), 'Voto registrado.')
  const retirar = () => acao(() => retirarVotoDenuncia(denuncia.id), 'Voto retirado.')
  const cancelar = () => acao(() => cancelarDenuncia(denuncia.id), 'Denúncia cancelada.')
  const resolver = (status) => acao(() => resolverDenuncia(denuncia.id, status),
    status === 'APROVADA' ? 'Denúncia aprovada.' : 'Denúncia rejeitada.')

  return (
    <Modal aberto={!!denuncia} onFechar={onFechar} titulo="Detalhe da denúncia">
      <div className="dn-modal">
        <div className="dn-modal-head">
          <div>
            <b>{denuncia.produtoNome || `Preço #${denuncia.precoId}`}</b>{' '}
            <span className="dn-price">{brl(denuncia.precoValor)}</span>
            <div className="dn-meta">{denuncia.mercadoNome || '—'} · denúncia #{denuncia.id}</div>
          </div>
        </div>

        <div className="dn-sec">
          <div className="dn-label">Motivo</div>
          <div>{denuncia.motivo}{denuncia.descricao ? ` — ${denuncia.descricao}` : ''}</div>
        </div>

        {denuncia.fotoPath && (
          <div className="dn-sec">
            <div className="dn-label">Foto enviada</div>
            {/* Mostra a original já (servida pelo backend); troca pelo thumb quando o worker terminar. */}
            <img className="dn-foto" alt="foto da denúncia"
                 src={urlImagem(denuncia.thumbPath || denuncia.fotoPath)} />
          </div>
        )}

        {pendente && (
          <div className="dn-sec">
            <div className="dn-pergunta">Este preço foi denunciado com razão?</div>
            <div className="dn-ajuda">Confirmada ou rejeitada com 3 votos da comunidade.</div>
            <div className="dn-votes">
              <button type="button" disabled={!denuncia.podeVotar || processando}
                      title={tooltip}
                      className={`dn-vote ${denuncia.meuVoto === 'CONFIRMA' ? 'active' : ''}`}
                      onClick={() => votar('CONFIRMA')}>Confirmar</button>
              <button type="button" disabled={!denuncia.podeVotar || processando}
                      title={tooltip}
                      className={`dn-vote no ${denuncia.meuVoto === 'REJEITA' ? 'active' : ''}`}
                      onClick={() => votar('REJEITA')}>Rejeitar</button>
            </div>
            <div className="dn-tally">
              <span>Confirmar: <b>{denuncia.votosConfirma}</b></span>
              <span>Rejeitar: <b>{denuncia.votosRejeita}</b></span>
              {jaVotei && <button type="button" className="dn-link" disabled={processando}
                                  onClick={retirar}>retirar meu voto</button>}
            </div>
          </div>
        )}

        {!pendente && (
          <div className="dn-sec dn-resolvida">
            {descricaoResolucao(denuncia)}
          </div>
        )}

        {isAdmin && pendente && (
          <div className="dn-sec dn-admin">
            <div className="dn-label" style={{ color: 'var(--cor-secundaria)' }}>Admin — override</div>
            <div className="dn-votes">
              <button type="button" className="dn-vote" disabled={processando}
                      onClick={() => resolver('APROVADA')}>Forçar aprovação</button>
              <button type="button" className="dn-vote no" disabled={processando}
                      onClick={() => resolver('REJEITADA')}>Forçar rejeição</button>
            </div>
          </div>
        )}

        {souDenunciante && pendente && (
          <div className="dn-modal-foot">
            <button type="button" className="dn-link" disabled={processando}
                    onClick={cancelar}>Cancelar minha denúncia</button>
          </div>
        )}
      </div>
    </Modal>
  )
}
