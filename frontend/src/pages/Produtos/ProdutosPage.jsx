import { useEffect, useMemo, useState } from 'react'
import { useAuth } from '../../context/AuthContext'
import { useToast } from '../../components/Toast/ToastContext'
import { getProdutos, getProdutosAdmin, getCategorias, inativarProduto, ativarProduto } from '../../services/api'
import Select from '../../components/Select/Select'
import Tabs from '../../components/Tabs/Tabs'
import Pagination from '../../components/Pagination/Pagination'
import ProdutosTabela from './ProdutosTabela'
import ProdutoCadastroModal from './ProdutoCadastroModal'
import Fab from '../../components/Fab/Fab'
import './Produtos.css'

const PAGE_SIZE = 10
const TABS_ATIVO = [{ value: 'ativos', label: 'Ativos' }, { value: 'todos', label: 'Todos' }]

export default function ProdutosPage() {
  const { usuario } = useAuth()
  const { mostrarToast } = useToast()
  const isAdmin = usuario?.tipo === 'ADMIN'

  const [produtos, setProdutos] = useState([])
  const [categorias, setCategorias] = useState([])
  const [carregando, setCarregando] = useState(true)
  const [erro, setErro] = useState(null)

  const [busca, setBusca] = useState('')
  const [buscaDebounced, setBuscaDebounced] = useState('')
  const [categoriaFiltro, setCategoriaFiltro] = useState('')
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
    const fonte = isAdmin ? getProdutosAdmin() : getProdutos()
    Promise.all([fonte, getCategorias()])
      .then(([prods, cats]) => {
        if (cancelado) return
        setProdutos(prods)
        setCategorias(cats)
      })
      .catch(e => { if (!cancelado) setErro(e.message) })
      .finally(() => { if (!cancelado) setCarregando(false) })
    return () => { cancelado = true }
  }, [isAdmin, recarregar])

  useEffect(() => {
    const t = setTimeout(() => setBuscaDebounced(busca.trim().toLowerCase()), 250)
    return () => clearTimeout(t)
  }, [busca])

  useEffect(() => { setPagina(0) }, [buscaDebounced, categoriaFiltro, filtroAtivo])

  const filtrados = useMemo(() => {
    return produtos.filter(p => {
      if (filtroAtivo === 'ativos' && !p.ativo) return false
      if (buscaDebounced && !p.nome.toLowerCase().includes(buscaDebounced)) return false
      if (categoriaFiltro && p.categoria !== categoriaFiltro) return false
      return true
    })
  }, [produtos, filtroAtivo, buscaDebounced, categoriaFiltro])

  const totalPaginas = Math.max(1, Math.ceil(filtrados.length / PAGE_SIZE))
  const paginaEfetiva = Math.min(pagina, totalPaginas - 1)
  const start = paginaEfetiva * PAGE_SIZE
  const visiveis = filtrados.slice(start, start + PAGE_SIZE)

  const opcoesCategoria = [
    { value: '', label: 'Todas as categorias' },
    ...categorias.map(c => ({ value: c.nome, label: c.nome })),
  ]

  async function onInativar(p) {
    setProcessandoId(p.id)
    try {
      const atualizado = await inativarProduto(p.id)
      setProdutos(ps => ps.map(x => x.id === p.id ? atualizado : x))
      mostrarToast('Produto inativado.')
    } catch (e) { mostrarToast(e.message, { tipo: 'erro' }) }
    finally { setProcessandoId(null) }
  }

  async function onAtivar(p) {
    setProcessandoId(p.id)
    try {
      const atualizado = await ativarProduto(p.id)
      setProdutos(ps => ps.map(x => x.id === p.id ? atualizado : x))
      mostrarToast('Produto reativado.')
    } catch (e) { mostrarToast(e.message, { tipo: 'erro' }) }
    finally { setProcessandoId(null) }
  }

  return (
    <>
      <div className="pr-cabecalho">
        <div>
          <h1>Produtos</h1>
          <p style={{ color: 'var(--cor-text-muted)' }}>Gerencie os produtos da cesta básica.</p>
        </div>
        {isAdmin && (
          <button type="button" className="pr-btn-cadastrar"
                  onClick={() => { setEmEdicao(null); setCadastroAberto(true) }}>
            Cadastrar produto
          </button>
        )}
      </div>

      <div className="pr-filtros">
        <input type="text" className="pr-busca" placeholder="Digite o nome do produto"
               value={busca} onChange={e => setBusca(e.target.value)} />
        <div className="pr-filtro-categoria">
          <Select id="pr-categoria" value={categoriaFiltro} onChange={setCategoriaFiltro}
                  options={opcoesCategoria} placeholder="Todas as categorias" />
        </div>
        {isAdmin && (
          <div className="pr-filtro-status">
            <Tabs items={TABS_ATIVO} value={filtroAtivo} onChange={setFiltroAtivo} />
          </div>
        )}
      </div>

      {erro && <p style={{ color: 'var(--cor-danger)' }}>Erro: {erro}</p>}

      {carregando
        ? <div className="pr-loading">Carregando...</div>
        : <>
            <ProdutosTabela produtos={visiveis} isAdmin={isAdmin}
                            onEditar={(p) => { setEmEdicao(p); setCadastroAberto(true) }}
                            onInativar={onInativar} onAtivar={onAtivar} processandoId={processandoId} />
            <div className="pr-rodape">
              <span className="pr-contagem">
                {filtrados.length === 0
                  ? 'Nenhum produto'
                  : `Mostrando ${start + 1} a ${Math.min(start + PAGE_SIZE, filtrados.length)} de ${filtrados.length} produtos`}
              </span>
              <Pagination total={filtrados.length} pageSize={PAGE_SIZE} page={paginaEfetiva} onChange={setPagina} />
            </div>
          </>}

      {isAdmin && (
        <ProdutoCadastroModal aberto={cadastroAberto} categorias={categorias} produto={emEdicao}
                              onFechar={() => { setCadastroAberto(false); setEmEdicao(null) }}
                              onSalvo={() => { setCadastroAberto(false); setEmEdicao(null); setRecarregar(n => n + 1) }}
                              onInativar={async (p) => { await onInativar(p); setCadastroAberto(false); setEmEdicao(null) }}
                              onAtivar={async (p) => { await onAtivar(p); setCadastroAberto(false); setEmEdicao(null) }} />
      )}

      {isAdmin && <Fab label="Cadastrar produto" onClick={() => { setEmEdicao(null); setCadastroAberto(true) }} />}
    </>
  )
}