const BADGE = {
  PENDENTE: { cls: 'dn-pend', label: 'Pendente' },
  APROVADA: { cls: 'dn-aprov', label: 'Aprovada' },
  REJEITADA: { cls: 'dn-rej', label: 'Rejeitada' },
  CANCELADA: { cls: 'dn-rej', label: 'Cancelada' },
}

function brl(valor) {
  if (valor == null) return '—'
  return Number(valor).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })
}

export default function DenunciasTabela({ denuncias, onAbrir, onVotar }) {
  if (!denuncias.length) {
    return <div className="dn-empty">Nenhuma denúncia encontrada.</div>
  }
  return (
    <div className="dn-tabela rt-wrap">
      <table className="responsive-table">
        <thead>
          <tr>
            <th>Produto / Mercado</th>
            <th>Preço</th>
            <th>Status</th>
            <th>Votos</th>
          </tr>
        </thead>
        <tbody>
          {denuncias.map(d => {
            const badge = BADGE[d.status] || { cls: '', label: d.status }
            return (
              <tr key={d.id} className="dn-row" onClick={() => onAbrir(d)}>
                <td>
                  <div className="dn-prod">{d.produtoNome || `Preço #${d.precoId}`}</div>
                  <div className="dn-meta">{d.mercadoNome || '—'}</div>
                </td>
                <td data-label="Preço" className="dn-price">{brl(d.precoValor)}</td>
                <td data-label="Status"><span className={`dn-badge ${badge.cls}`}>{badge.label}</span></td>
                <td data-label="Votos" className="dn-meta">
                  <span className="dn-votos-desktop">👍 {d.votosConfirma} · 👎 {d.votosRejeita}</span>
                  <span className="dn-votos-mobile">
                    <button
                      className={`dn-votebtn ${d.meuVoto === 'CONFIRMA' ? 'ativo' : ''}`}
                      disabled={!d.podeVotar}
                      onClick={(e) => { e.stopPropagation(); onVotar(d, 'CONFIRMA') }}
                    >👍 {d.votosConfirma}</button>
                    <button
                      className={`dn-votebtn no ${d.meuVoto === 'REJEITA' ? 'ativo' : ''}`}
                      disabled={!d.podeVotar}
                      onClick={(e) => { e.stopPropagation(); onVotar(d, 'REJEITA') }}
                    >👎 {d.votosRejeita}</button>
                  </span>
                </td>
              </tr>
            )
          })}
        </tbody>
      </table>
    </div>
  )
}
