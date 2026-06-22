import ProdutoRow from './ProdutoRow'

export default function ProdutosTable({ produtos, onSelecionar }) {
  if (!produtos.length) {
    return <div className="vg-empty">Nenhum produto encontrado.</div>
  }
  return (
    <div className="vg-tabela rt-wrap">
      <table className="responsive-table">
        <thead>
          <tr>
            <th>Produto</th>
            <th>Tendência (30d)</th>
            <th>Menor preço</th>
            <th>Variação</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          {produtos.map(p => (
            <ProdutoRow key={p.id} produto={p} onClick={onSelecionar} />
          ))}
        </tbody>
      </table>
    </div>
  )
}