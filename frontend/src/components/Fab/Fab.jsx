import './Fab.css'

// Botão flutuante de ação (FAB) — visível apenas no mobile (≤768px).
// No desktop o botão "Cadastrar X" com texto continua no cabeçalho.
export default function Fab({ onClick, label }) {
  return (
    <button type="button" className="fab-add" onClick={onClick} aria-label={label} title={label}>
      <span aria-hidden="true">+</span>
    </button>
  )
}
