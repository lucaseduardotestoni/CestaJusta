import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { getProdutos, getComparacaoProduto } from '../services/api'
import './DashboardPage.css'

export default function DashboardPage() {
  const [produtos, setProdutos] = useState([])
  const [comparacoes, setComparacoes] = useState({})
  const [busca, setBusca] = useState('')
  const [filtro, setFiltro] = useState('media')
  const [loading, setLoading] = useState(true)
  const { logout } = useAuth()
  const navigate = useNavigate()

  useEffect(() => {
    async function carregar() {
      try {
        const lista = await getProdutos()
        setProdutos(lista)
        const map = {}
        await Promise.all(
          lista.map(async (p) => {
            try { map[p.id] = await getComparacaoProduto(p.id) } catch { /* sem preços */ }
          })
        )
        setComparacoes(map)
      } catch (err) {
        if (err.message?.includes('401') || err.message?.includes('403')) {
          logout(); navigate('/')
        }
      } finally {
        setLoading(false)
      }
    }
    carregar()
  }, [])

  function handleLogout() {
    logout()
    navigate('/')
  }

  function getInfoCard(produto) {
    const comp = comparacoes[produto.id]
    if (!comp || !comp.precosPorMercado?.length) return null
    const precos = comp.precosPorMercado
    if (filtro === 'baixo') {
      const entry = precos.reduce((a, b) => a.valor < b.valor ? a : b)
      return { valor: entry.valor, loja: entry.mercadoNomeFantasia, data: entry.dataColeta }
    }
    if (filtro === 'alto') {
      const entry = precos.reduce((a, b) => a.valor > b.valor ? a : b)
      return { valor: entry.valor, loja: entry.mercadoNomeFantasia, data: entry.dataColeta }
    }
    const entry = precos.reduce((a, b) => a.valor < b.valor ? a : b)
    return { valor: comp.precoMedio, loja: `${comp.totalMercados} mercado${comp.totalMercados !== 1 ? 's' : ''}`, data: entry.dataColeta }
  }

  function produtosFiltrados() {
    return produtos
      .filter(p => p.nome.toLowerCase().includes(busca.toLowerCase()))
      .filter(p => comparacoes[p.id])
      .sort((a, b) => {
        const ca = comparacoes[a.id], cb = comparacoes[b.id]
        if (!ca) return 1; if (!cb) return -1
        if (filtro === 'alto') return cb.maiorPreco - ca.maiorPreco
        if (filtro === 'baixo') return ca.menorPreco - cb.menorPreco
        return ca.precoMedio - cb.precoMedio
      })
  }

  function formatPreco(valor) {
    if (valor == null) return '-'
    return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(valor)
  }

  function formatData(data) {
    if (!data) return ''
    return new Date(data + 'T00:00:00').toLocaleDateString('pt-BR', { day: '2-digit', month: 'short' })
  }

  const lista = produtosFiltrados()

  return (
    <div className="db-page">
      <nav className="db-nav">
        <div className="db-nav-links">
          <a className="db-nav-link db-nav-active">Início</a>
          <a className="db-nav-link">Sobre nós</a>
          <a className="db-nav-link">Produtos</a>
          <a className="db-nav-link">Denúncias</a>
        </div>
        <button className="db-nav-logout" onClick={handleLogout}>Sair</button>
      </nav>

      <main className="db-main">
        <div className="db-search-wrap">
          <input
            className="db-search"
            type="text"
            placeholder="Pesquisar produto..."
            value={busca}
            onChange={e => setBusca(e.target.value)}
          />
          <span className="db-search-icon">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#E85C35" strokeWidth="2.5">
              <circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/>
            </svg>
          </span>
        </div>

        <div className="db-filtros">
          <button className={`db-filtro ${filtro === 'baixo' ? 'db-filtro-ativo' : ''}`} onClick={() => setFiltro('baixo')}>
            Menor preço
          </button>
          <button className={`db-filtro ${filtro === 'media' ? 'db-filtro-ativo' : ''}`} onClick={() => setFiltro('media')}>
            Preço médio
          </button>
          <button className={`db-filtro ${filtro === 'alto' ? 'db-filtro-ativo' : ''}`} onClick={() => setFiltro('alto')}>
            Maior preço
          </button>
        </div>

        {loading ? (
          <div className="db-loading">
            <span className="db-spinner" />
            <p>Carregando produtos...</p>
          </div>
        ) : lista.length === 0 ? (
          <div className="db-empty">
            <svg width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="#ccc" strokeWidth="1.5">
              <circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/>
            </svg>
            <p>{busca ? `Nenhum resultado para "${busca}"` : 'Nenhum produto com preços cadastrados.'}</p>
          </div>
        ) : (
          <div className="db-lista">
            {lista.map(produto => {
              const info = getInfoCard(produto)
              return (
                <div key={produto.id} className="db-card">
                  <div className="db-card-img">
                    <span className="db-card-categoria">{produto.categoria}</span>
                  </div>
                  <div className="db-card-info">
                    <span className="db-card-nome">{produto.nome}</span>
                    {produto.marca && (
                      <span className="db-card-meta">
                        {produto.marca}{produto.unidadeMedida ? ` · ${produto.unidadeMedida}` : ''}
                      </span>
                    )}
                    {info?.data && <span className="db-card-data">{formatData(info.data)}</span>}
                    {info?.loja && <span className="db-card-loja">{info.loja}</span>}
                  </div>
                  <span className="db-card-preco">{formatPreco(info?.valor)}</span>
                </div>
              )
            })}
          </div>
        )}
      </main>
    </div>
  )
}
