import { useCallback, useEffect, useMemo, useState } from 'react'
import { getMinhasDenuncias, getDenuncias, votarDenuncia } from '../../services/api'
import { useToast } from '../../components/Toast/ToastContext'
import FiltroStatusChips from './FiltroStatusChips'
import DenunciasTabela from './DenunciasTabela'
import DenunciaDetalheModal from './DenunciaDetalheModal'
import './Denuncias.css'

export default function DenunciasPage({ escopo }) {
  const minhas = escopo === 'minhas'
  const [denuncias, setDenuncias] = useState([])
  const [carregando, setCarregando] = useState(true)
  const [erro, setErro] = useState(null)
  const [statusFiltro, setStatusFiltro] = useState('')
  const [abertaId, setAbertaId] = useState(null)
  const [recarregar, setRecarregar] = useState(0)
  const { mostrarToast } = useToast()

  // "Todas": filtro server-side (status). "Minhas": busca tudo e filtra no cliente.
  useEffect(() => {
    let cancelado = false
    setCarregando(true)
    setErro(null)
    const fonte = minhas ? getMinhasDenuncias() : getDenuncias(statusFiltro || undefined)
    fonte
      .then(lista => { if (!cancelado) setDenuncias(lista) })
      .catch(e => { if (!cancelado) setErro(e.message) })
      .finally(() => { if (!cancelado) setCarregando(false) })
    return () => { cancelado = true }
  }, [minhas, statusFiltro, recarregar])

  const visiveis = useMemo(() => {
    if (!minhas || !statusFiltro) return denuncias
    return denuncias.filter(d => d.status === statusFiltro)
  }, [denuncias, minhas, statusFiltro])

  const aberta = useMemo(
    () => denuncias.find(d => d.id === abertaId) || null,
    [denuncias, abertaId],
  )

  const onMudou = useCallback(() => setRecarregar(n => n + 1), [])

  async function onVotar(d, tipo) {
    try {
      await votarDenuncia(d.id, tipo)
      mostrarToast('Voto registrado.')
      setRecarregar(n => n + 1)
    } catch (e) { mostrarToast(e.message, { tipo: 'erro' }) }
  }

  return (
    <>
      <div className="dn-cabecalho">
        <h1>{minhas ? 'Minhas denúncias' : 'Denúncias'}</h1>
        <p style={{ color: 'var(--cor-text-muted)' }}>
          {minhas
            ? 'As denúncias que você criou e o status de cada uma.'
            : 'Ajude a comunidade a validar os preços denunciados.'}
        </p>
      </div>

      <FiltroStatusChips valor={statusFiltro} onChange={setStatusFiltro} />

      {erro && <p style={{ color: 'var(--cor-danger)' }}>Erro: {erro}</p>}

      {carregando
        ? <div className="dn-loading">Carregando...</div>
        : <DenunciasTabela denuncias={visiveis} onAbrir={(d) => setAbertaId(d.id)} onVotar={onVotar} />}

      <DenunciaDetalheModal
        denuncia={aberta}
        onFechar={() => setAbertaId(null)}
        onMudou={onMudou}
      />
    </>
  )
}
