import { useState } from 'react'
import useDashboard from '../../hooks/useDashboard'
import { useAuth } from '../../context/AuthContext'
import KpiSection from './KpiSection'
import ProdutosTable from './ProdutosTable'
import ProdutoDetalheModal from './ProdutoDetalheModal'
import Tabs from '../../components/Tabs/Tabs'
import Pagination from '../../components/Pagination/Pagination'
import './VisaoGeral.css'

const TABS = [
  { value: 'todos',  label: 'Todos os produtos' },
  { value: 'quedas', label: 'Maiores quedas' },
  { value: 'altas',  label: 'Maiores altas' },
]
const PAGE_SIZE = 10

export default function VisaoGeralPage() {
  const { kpis, produtos, loading, error, ordem, setOrdem } = useDashboard()
  const { usuario } = useAuth()
  const [pagina, setPagina] = useState(0)
  const [selecionado, setSelecionado] = useState(null)

  function trocarOrdem(nova) {
    setOrdem(nova)
    setPagina(0)
  }

  const start = pagina * PAGE_SIZE
  const visiveis = produtos.slice(start, start + PAGE_SIZE)
  const nome = usuario?.nome ? usuario.nome.split(' ')[0] : ''

  return (
    <>
      <h1>Olá{nome ? `, ${nome}!` : '!'} </h1>
      <p style={{ color: 'var(--cor-text-muted)' }}>
        Veja os preços e economize na sua região.
      </p>

      <KpiSection kpis={kpis} />

      <div className="vg-toolbar">
        <Tabs items={TABS} value={ordem} onChange={trocarOrdem} />
      </div>

      {error && <p style={{ color: 'var(--cor-danger)' }}>Erro: {error}</p>}

      {loading
        ? <div className="vg-loading">Carregando...</div>
        : <>
            <ProdutosTable produtos={visiveis} onSelecionar={setSelecionado} />
            <Pagination total={produtos.length} pageSize={PAGE_SIZE}
                        page={pagina} onChange={setPagina} />
          </>
      }

      <ProdutoDetalheModal produto={selecionado} onFechar={() => setSelecionado(null)} />
    </>
  )
}
