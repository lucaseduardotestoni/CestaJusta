export default function MercadosTabela({ mercados, isAdmin, onEditar, onInativar, onAtivar, processandoId }) {
  if (!mercados.length) {
    return <div className="mc-empty">Nenhum mercado encontrado.</div>
  }
  return (
    <div className="mc-tabela">
      <table>
        <thead>
          <tr>
            <th>Nome fantasia</th>
            <th>CNPJ</th>
            <th>Cidade / UF</th>
            {isAdmin && <th>Ações</th>}
          </tr>
        </thead>
        <tbody>
          {mercados.map(m => (
            <tr key={m.id} className={m.ativo ? '' : 'mc-inativo'}>
              <td>
                <span className="mc-nome">{m.nomeFantasia}</span>
                {!m.ativo && <span className="mc-selo-inativo">Inativo</span>}
              </td>
              <td>{m.cnpj || '—'}</td>
              <td>{m.cidade ? `${m.cidade}${m.estado ? ` / ${m.estado}` : ''}` : (m.estado || '—')}</td>
              {isAdmin && (
                <td>
                  <button type="button" className="mc-acao mc-acao-editar"
                          disabled={processandoId === m.id} onClick={() => onEditar(m)}>
                    Editar
                  </button>
                  {m.ativo
                    ? <button type="button" className="mc-acao mc-acao-inativar"
                              disabled={processandoId === m.id} onClick={() => onInativar(m)}>
                        Inativar
                      </button>
                    : <button type="button" className="mc-acao mc-acao-ativar"
                              disabled={processandoId === m.id} onClick={() => onAtivar(m)}>
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
