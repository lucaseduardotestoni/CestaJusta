import { useEffect, useMemo, useState } from 'react'
import { useAuth } from '../../context/AuthContext'
import { useToast } from '../../components/Toast/ToastContext'
import { getMercados, getMercadosAdmin, inativarMercado, ativarMercado } from '../../services/api'
import Tabs from '../../components/Tabs/Tabs'
import Pagination from '../../components/Pagination/Pagination'
import MercadosTabela from './MercadosTabela'
import MercadoCadastroModal from './MercadoCadastroModal'
import Fab from '../../components/Fab/Fab'
import './Mercados.css'

const PAGE_SIZE = 10
const TABS_ATIVO = [{ value: 'ativos', label: 'Ativos' }, { value: 'todos', label: 'Todos' }]

export default function MercadosPage() {
  const { usuario } = useAuth()
  const { mostrarToast } = useToast()
  const isAdmin = usuario?.tipo === 'ADMIN'

  const [mercados, setMercados] = useState([])
  const [carregando, setCarregando] = useState(true)
  const [erro, setErro] = useState(null)

  const [busca, setBusca] = useState('')
  const [buscaDebounced, setBuscaDebounced] = useState('')
  const [filtroAtivo, setFiltroAtivo] = useState('ativos')
  const [pagina, setPagina] = useState(0)
  const [cadastroAberto, setCadastroAberto] = useState(false)
  const [emEdicao, setEmEdicao] = useState(null)
  const [processandoId, setProcessandoId] = useState(null)
  const [recarregar, setRecarregar] = useState(0)

  useEffect(() => {
    let cancelado = false
    setCarregando(true)
    setErro(null)
    const fonte = isAdmin ? getMercadosAdmin() : getMercados()
    fonte
      .then(lista => { if (!cancelado) setMercados(lista) })
      .catch(e => { if (!cancelado) setErro(e.message) })
      .finally(() => { if (!cancelado) setCarregando(false) })
    return () => { cancelado = true }
  }, [isAdmin, recarregar])

  useEffect(() => {
    const t = setTimeout(() => setBuscaDebounced(busca.trim().toLowerCase()), 250)
    return () => clearTimeout(t)
  }, [busca])

  useEffect(() => { setPagina(0) }, [buscaDebounced, filtroAtivo])

  const filtrados = useMemo(() => {
    return mercados.filter(m => {
      if (filtroAtivo === 'ativos' && !m.ativo) return false
      if (buscaDebounced && !m.nomeFantasia.toLowerCase().includes(buscaDebounced)) return false
      return true
    })
  }, [mercados, filtroAtivo, buscaDebounced])

  const totalPaginas = Math.max(1, Math.ceil(filtrados.length / PAGE_SIZE))
  const paginaEfetiva = Math.min(pagina, totalPaginas - 1)
  const start = paginaEfetiva * PAGE_SIZE
  const visiveis = filtrados.slice(start, start + PAGE_SIZE)

  async function onInativar(m) {
    setProcessandoId(m.id)
    try {
      const atualizado = await inativarMercado(m.id)
      setMercados(ms => ms.map(x => x.id === m.id ? atualizado : x))
      mostrarToast('Mercado inativado.')
    } catch (e) { mostrarToast(e.message, { tipo: 'erro' }) }
    finally { setProcessandoId(null) }
  }

  async function onAtivar(m) {
    setProcessandoId(m.id)
    try {
      const atualizado = await ativarMercado(m.id)
      setMercados(ms => ms.map(x => x.id === m.id ? atualizado : x))
      mostrarToast('Mercado reativado.')
    } catch (e) { mostrarToast(e.message, { tipo: 'erro' }) }
    finally { setProcessandoId(null) }
  }

  return (
    <>
      <div className="mc-cabecalho">
        <div>
          <h1>Mercados</h1>
          <p style={{ color: 'var(--cor-text-muted)' }}>Gerencie os mercados cadastrados.</p>
        </div>
        {isAdmin && (
          <button type="button" className="mc-btn-cadastrar"
                  onClick={() => { setEmEdicao(null); setCadastroAberto(true) }}>
            Cadastrar mercado
          </button>
        )}
      </div>

      <div className="mc-filtros">
        <input type="text" className="mc-busca" placeholder="Digite o nome do mercado"
               value={busca} onChange={e => setBusca(e.target.value)} />
        {isAdmin && (
          <div className="mc-filtro-status">
            <Tabs items={TABS_ATIVO} value={filtroAtivo} onChange={setFiltroAtivo} />
          </div>
        )}
      </div>

      {erro && <p style={{ color: 'var(--cor-danger)' }}>Erro: {erro}</p>}

      {carregando
        ? <div className="mc-loading">Carregando...</div>
        : <>
            <MercadosTabela mercados={visiveis} isAdmin={isAdmin}
                            onEditar={(m) => { setEmEdicao(m); setCadastroAberto(true) }}
                            onInativar={onInativar} onAtivar={onAtivar} processandoId={processandoId} />
            <div className="mc-rodape">
              <span className="mc-contagem">
                {filtrados.length === 0
                  ? 'Nenhum mercado'
                  : `Mostrando ${start + 1} a ${Math.min(start + PAGE_SIZE, filtrados.length)} de ${filtrados.length} mercados`}
              </span>
              <Pagination total={filtrados.length} pageSize={PAGE_SIZE} page={paginaEfetiva} onChange={setPagina} />
            </div>
          </>}

      {isAdmin && (
        <MercadoCadastroModal aberto={cadastroAberto} mercado={emEdicao}
                              onFechar={() => { setCadastroAberto(false); setEmEdicao(null) }}
                              onSalvo={() => { setCadastroAberto(false); setEmEdicao(null); setRecarregar(n => n + 1) }}
                              onInativar={async (m) => { await onInativar(m); setCadastroAberto(false); setEmEdicao(null) }}
                              onAtivar={async (m) => { await onAtivar(m); setCadastroAberto(false); setEmEdicao(null) }} />
      )}

      {isAdmin && <Fab label="Cadastrar mercado" onClick={() => { setEmEdicao(null); setCadastroAberto(true) }} />}
    </>
  )
}
