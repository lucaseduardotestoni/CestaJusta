// Retorna true quando a viewport está na faixa mobile (≤ 768px).
// Usado para ativar interações exclusivas do mobile (ex.: tocar no card abre edição)
// sem alterar o comportamento do desktop. Avaliado no momento do clique.
export const isMobileViewport = () => window.matchMedia('(max-width: 768px)').matches
