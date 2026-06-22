import { PAPEIS, ROTULO_PAPEL } from './papeis'
import { isMobileViewport } from '../../utils/viewport'

export default function UsuariosTabela({ usuarios, onAlterarPapel, onInativar, onAtivar, onEditar, processandoId }) {
  if (!usuarios.length) {
    return <div className="us-empty">Nenhum usuário encontrado.</div>
  }
  return (
    <div className="us-tabela rt-wrap">
      <table className="responsive-table responsive-table-tappable">
        <thead>
          <tr>
            <th>Nome</th>
            <th>E-mail</th>
            <th>Papel</th>
            <th>Ações</th>
          </tr>
        </thead>
        <tbody>
          {usuarios.map(u => (
            <tr key={u.id} className={u.ativo ? '' : 'us-inativo'}
                onClick={() => { if (isMobileViewport()) onEditar(u) }}>
              <td>
                <span className="us-nome">{u.nome}</span>
                {!u.ativo && <span className="us-selo-inativo">Inativo</span>}
              </td>
              <td data-label="E-mail">{u.email}</td>
              <td data-label="Papel">
                <select
                  className="us-papel-select"
                  value={u.tipoUsuario}
                  disabled={processandoId === u.id || !u.ativo}
                  onChange={e => onAlterarPapel(u, e.target.value)}
                >
                  {PAPEIS.map(p => <option key={p.value} value={p.value}>{p.label}</option>)}
                </select>
                <span className="us-papel-mobile">{ROTULO_PAPEL[u.tipoUsuario] || u.tipoUsuario}</span>
              </td>
              <td data-label="Ações" className="rt-acoes">
                {u.ativo
                  ? <button type="button" className="us-acao us-acao-inativar"
                            disabled={processandoId === u.id} onClick={() => onInativar(u)}>
                      Inativar
                    </button>
                  : <button type="button" className="us-acao us-acao-ativar"
                            disabled={processandoId === u.id} onClick={() => onAtivar(u)}>
                      Reativar
                    </button>}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}