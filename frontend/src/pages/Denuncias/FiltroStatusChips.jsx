const OPCOES = [
  { value: '', label: 'Todas' },
  { value: 'PENDENTE', label: 'Pendentes' },
  { value: 'APROVADA', label: 'Aprovadas' },
  { value: 'REJEITADA', label: 'Rejeitadas' },
]

export default function FiltroStatusChips({ valor, onChange }) {
  return (
    <div className="dn-chips">
      {OPCOES.map(o => (
        <button
          key={o.value || 'todas'}
          type="button"
          className={`dn-chip ${valor === o.value ? 'on' : ''}`}
          onClick={() => onChange(o.value)}
        >
          {o.label}
        </button>
      ))}
    </div>
  )
}
