import { urlImagem } from './urlImagem'

export default function ProdutosTabela({ produtos, isAdmin, onEditar, onInativar, onAtivar, processandoId }) {
  if (!produtos.length) {
    return <div className="pr-empty">Nenhum produto encontrado.</div>
  }
  return (
    <div className="pr-tabela">
      <table>
        <thead>
          <tr>
            <th>Produto</th>
            <th>Categoria</th>
            <th>Código de barras</th>
            {isAdmin && <th>Ações</th>}
          </tr>
        </thead>
        <tbody>
          {produtos.map(p => (
            <tr key={p.id} className={p.ativo ? '' : 'pr-inativo'}>
              <td>
                <div className="pr-produto-info">
                  {(p.thumbPath || p.imagemPath)
                    ? <img src={urlImagem(p.thumbPath || p.imagemPath)} alt="" className="pr-produto-img"
                           onError={(e) => { e.currentTarget.style.visibility = 'hidden' }} />
                    : <div className="pr-produto-img" />}
                  <div>
                    <div className="pr-produto-nome">
                      {p.nome}
                      {!p.ativo && <span className="pr-selo-inativo">Inativo</span>}
                    </div>
                    <div className="pr-produto-meta">
                      {p.marca}{p.unidadeMedida ? ` · ${p.unidadeMedida}` : ''}
                    </div>
                  </div>
                </div>
              </td>
              <td>{p.categoria || '—'}</td>
              <td>{p.codigoBarras || '—'}</td>
              {isAdmin && (
                <td>
                  <button type="button" className="pr-acao pr-acao-editar"
                          disabled={processandoId === p.id} onClick={() => onEditar(p)}>
                    Editar
                  </button>
                  {p.ativo
                    ? <button type="button" className="pr-acao pr-acao-inativar"
                              disabled={processandoId === p.id} onClick={() => onInativar(p)}>
                        Inativar
                      </button>
                    : <button type="button" className="pr-acao pr-acao-ativar"
                              disabled={processandoId === p.id} onClick={() => onAtivar(p)}>
                        Reativar
                      </button>}
                </td>
              )}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}