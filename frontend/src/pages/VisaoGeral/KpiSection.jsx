import KpiCard from '../../components/KpiCard/KpiCard'

export default function KpiSection({ kpis }) {
  if (!kpis) return null
  return (
    <div className="vg-kpis">
      <KpiCard titulo="Cesta básica (semana)" valor={kpis.valorCesta}
               variacao={kpis.variacaoSemanal} formato="brl" />
      <KpiCard titulo="Produtos cadastrados" valor={kpis.totalProdutos} />
      <KpiCard titulo="Mercados ativos"     valor={kpis.totalMercados} />
      <KpiCard titulo="Economia média"      valor={kpis.economiaMedia} formato="percent" />
    </div>
  )
}