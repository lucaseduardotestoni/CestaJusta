import { createContext, useCallback, useContext, useMemo, useState } from 'react'

/**
 * Guarda, em memória e pelo tempo da sessão, quais preços o usuário já
 * confirmou ou denunciou. Vive acima do modal de detalhes para que a ação
 * sobreviva ao fechar/reabrir o modal (o estado local do PrecoLinha não
 * sobreviveria, pois o modal desmonta). Reseta no reload da página — a regra
 * durável é autoritativa no backend (anti-spam / "já confirmou").
 */
const AcoesPrecoSessaoContext = createContext(null)

export function useAcoesPrecoSessao() {
  const ctx = useContext(AcoesPrecoSessaoContext)
  if (!ctx) throw new Error('useAcoesPrecoSessao deve ser usado dentro de <AcoesPrecoSessaoProvider>')
  return ctx
}

export function AcoesPrecoSessaoProvider({ children }) {
  const [confirmados, setConfirmados] = useState(() => new Set())
  const [denunciados, setDenunciados] = useState(() => new Set())

  const marcarConfirmado = useCallback((precoId) => {
    setConfirmados(prev => new Set(prev).add(precoId))
  }, [])
  const marcarDenunciado = useCallback((precoId) => {
    setDenunciados(prev => new Set(prev).add(precoId))
  }, [])
  const marcarDenunciados = useCallback((precoIds) => {
    setDenunciados(prev => {
      const next = new Set(prev)
      precoIds.forEach(id => next.add(id))
      return next
    })
  }, [])

  const value = useMemo(() => ({
    foiConfirmado: (precoId) => confirmados.has(precoId),
    foiDenunciado: (precoId) => denunciados.has(precoId),
    marcarConfirmado,
    marcarDenunciado,
    marcarDenunciados,
  }), [confirmados, denunciados, marcarConfirmado, marcarDenunciado, marcarDenunciados])

  return (
    <AcoesPrecoSessaoContext.Provider value={value}>
      {children}
    </AcoesPrecoSessaoContext.Provider>
  )
}