import { useEffect, useMemo, useState } from 'react'
import { useAuth } from '../../context/AuthContext'
import { useToast } from '../../components/Toast/ToastContext'
import { getUsuarios, alterarPapelUsuario, inativarUsuario, ativarUsuario } from '../../services/api'
import Tabs from '../../components/Tabs/Tabs'
import Pagination from '../../components/Pagination/Pagination'
import UsuariosTabela from './UsuariosTabela'
import UsuarioCadastroModal from './UsuarioCadastroModal'
import Fab from '../../components/Fab/Fab'
import { ROTULO_PAPEL } from './papeis'
import './Usuarios.css'

const PAGE_SIZE = 10
const TABS_ATIVO = [{ value: 'ativos', label: 'Ativos' }, { value: 'todos', label: 'Todos' }]

export default function UsuariosPage() {
  const { usuario } = useAuth()
  const { mostrarToast } = useToast()
  const isAdmin = usuario?.tipo === 'ADMIN'

  const [usuarios, setUsuarios] = useState([])
  const [carregando, setCarregando] = useState(true)
  const [erro, setErro] = useState(null)

  const [busca, setBusca] = useState('')
  const [buscaDebounced, setBuscaDebounced] = useState('')
  const [filtroAtivo, setFiltroAtivo] = useState('ativos')
  const [pagina, setPagina] = useState(0)
  const [cadastroAberto, setCadastroAberto] = useState(false)
  const [processandoId, setProcessandoId] = useState(null)
  const [recarregar, setRecarregar] = useState(0)

  useEffect(() => {
    if (!isAdmin) { setCarregando(false); return }
    let cancelado = false
    setCarregando(true)
    setErro(null)
    getUsuarios()
      .then(lista => { if (!cancelado) setUsuarios(lista) })
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
    return usuarios.filter(u => {
      if (filtroAtivo === 'ativos' && !u.ativo) return false
      if (buscaDebounced &&
          !u.nome.toLowerCase().includes(buscaDebounced) &&
          !u.email.toLowerCase().includes(buscaDebounced)) return false
      return true
    })
  }, [usuarios, filtroAtivo, buscaDebounced])

  const totalPaginas = Math.max(1, Math.ceil(filtrados.length / PAGE_SIZE))
  const paginaEfetiva = Math.min(pagina, totalPaginas - 1)
  const start = paginaEfetiva * PAGE_SIZE
  const visiveis = filtrados.slice(start, start + PAGE_SIZE)

  async function onAlterarPapel(u, novoTipo) {
    if (novoTipo === u.tipoUsuario) return
    setProcessandoId(u.id)
    try {
      const atualizado = await alterarPapelUsuario(u.id, novoTipo)
      setUsuarios(us => us.map(x => x.id === u.id ? atualizado : x))
      mostrarToast(`Papel alterado para ${ROTULO_PAPEL[novoTipo] || novoTipo}.`)
    } catch (e) { mostrarToast(e.message, { tipo: 'erro' }) }
    finally { setProcessandoId(null) }
  }

  async function onInativar(u) {
    setProcessandoId(u.id)
    try {
      const atualizado = await inativarUsuario(u.id)
      setUsuarios(us => us.map(x => x.id === u.id ? atualizado : x))
      mostrarToast('Usuário inativado.')
    } catch (e) { mostrarToast(e.message, { tipo: 'erro' }) }
    finally { setProcessandoId(null) }
  }

  async function onAtivar(u) {
    setProcessandoId(u.id)
    try {
      const atualizado = await ativarUsuario(u.id)
      setUsuarios(us => us.map(x => x.id === u.id ? atualizado : x))
      mostrarToast('Usuário reativado.')
    } catch (e) { mostrarToast(e.message, { tipo: 'erro' }) }
    finally { setProcessandoId(null) }
  }

  if (!isAdmin) {
    return (
      <>
        <h1>Usuários</h1>
        <div className="us-restrito">Acesso restrito a administradores.</div>
      </>
    )
  }

  return (
    <>
      <div className="us-cabecalho">
        <div>
          <h1>Usuários</h1>
          <p style={{ color: 'var(--cor-text-muted)' }}>Gerencie as contas e os papéis dos usuários.</p>
        </div>
        <button type="button" className="us-btn-cadastrar" onClick={() => setCadastroAberto(true)}>
          Cadastrar usuário
        </button>
      </div>

      <div className="us-filtros">
        <input type="text" className="us-busca" placeholder="Busque por nome ou e-mail"
               value={busca} onChange={e => setBusca(e.target.value)} />
        <div className="us-filtro-status">
          <Tabs items={TABS_ATIVO} value={filtroAtivo} onChange={setFiltroAtivo} />
        </div>
      </div>

      {erro && <p style={{ color: 'var(--cor-danger)' }}>Erro: {erro}</p>}

      {carregando
        ? <div className="us-loading">Carregando...</div>
        : <>
            <UsuariosTabela usuarios={visiveis}
                            onAlterarPapel={onAlterarPapel}
                            onInativar={onInativar} onAtivar={onAtivar} processandoId={processandoId} />
            <div className="us-rodape">
              <span className="us-contagem">
                {filtrados.length === 0
                  ? 'Nenhum usuário'
                  : `Mostrando ${start + 1} a ${Math.min(start + PAGE_SIZE, filtrados.length)} de ${filtrados.length} usuários`}
              </span>
              <Pagination total={filtrados.length} pageSize={PAGE_SIZE} page={paginaEfetiva} onChange={setPagina} />
            </div>
          </>}

      <UsuarioCadastroModal aberto={cadastroAberto}
                            onFechar={() => setCadastroAberto(false)}
                            onSalvo={() => { setCadastroAberto(false); setRecarregar(n => n + 1) }} />

      <Fab label="Cadastrar usuário" onClick={() => setCadastroAberto(true)} />
    </>
  )
}