import './Pagination.css'

export default function Pagination({ total, pageSize, page, onChange }) {
  const totalPages = Math.max(1, Math.ceil(total / pageSize))
  if (totalPages <= 1) return null

  function ir(novo) {
    const clamped = Math.max(0, Math.min(totalPages - 1, novo))
    if (clamped !== page) onChange(clamped)
  }

  return (
    <div className="pagination">
      <button onClick={() => ir(page - 1)} disabled={page === 0}>«</button>
      <span className="pagination-status">
        Página {page + 1} de {totalPages}
      </span>
      <button onClick={() => ir(page + 1)} disabled={page >= totalPages - 1}>»</button>
    </div>
  )
}
