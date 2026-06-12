const CONFIG = {
  PENDENTE:      { dot: 'dot-pend', badge: 'badge-pend', rotulo: 'pendente' },
  CONFIRMADO:    { dot: 'dot-conf', badge: 'badge-conf', rotulo: 'confirmado' },
  DESATUALIZADO: { dot: 'dot-desat', badge: 'badge-desat', rotulo: 'desatualizado' },
  REJEITADO:     { dot: 'dot-rej', badge: 'badge-rej', rotulo: 'rejeitado' },
}

export function StatusDot({ status }) {
  const cfg = CONFIG[status] || CONFIG.PENDENTE
  return <span className={`pm-dot ${cfg.dot}`} aria-hidden="true" />
}

export function StatusBadge({ status }) {
  const cfg = CONFIG[status] || CONFIG.PENDENTE
  return <span className={`pm-badge ${cfg.badge}`}>{cfg.rotulo}</span>
}
