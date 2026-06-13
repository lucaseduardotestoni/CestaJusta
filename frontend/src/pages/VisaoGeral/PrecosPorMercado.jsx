import { useEffect, useState } from 'react'
import { getPrecosPorProduto, getMeusPrecosDenunciados } from '../../services/api'
import { useAcoesPrecoSessao } from '../../context/AcoesPrecoSessaoContext'
import PrecoLinha from './PrecoLinha'

export default function PrecosPorMercado({ produtoId }) {
  const [precos, setPrecos] = useState(null)
  const [erro, setErro] = useState(null)
  const { marcarDenunciados } = useAcoesPrecoSessao()

  useEffect(() => {
    if (!produtoId) return
    let cancelado = false
    setPrecos(null)
    setErro(null)
    getPrecosPorProduto(produtoId)
      .then(lista => { if (!cancelado) setPrecos(lista) })
      .catch(e => { if (!cancelado) setErro(e.message) })
    // sincroniza com o backend quais preços este usuário já denunciou (janela anti-spam),
    // para o botão "Denunciar" não reaparecer após reload. Falha silenciosa: degrada para
    // bloqueio só em sessão + erro do backend ao reenviar.
    getMeusPrecosDenunciados()
      .then(ids => { if (!cancelado && Array.isArray(ids)) marcarDenunciados(ids) })
      .catch(() => {})
    return () => { cancelado = true }
  }, [produtoId, marcarDenunciados])

  if (erro) return <p className="pm-erro">Não foi possível carregar os preços: {erro}</p>
  if (!precos) return <p>Carregando preços…</p>
  if (precos.length === 0) return <p className="pm-meta">Nenhum preço cadastrado para este produto.</p>

  const ordenados = precos.slice().sort((a, b) => a.valor - b.valor)

  return (
    <>
      {ordenados.map(p => <PrecoLinha key={p.id} preco={p} />)}
      <div className="pm-legenda">
        <span><span className="pm-dot dot-conf" /> Confirmado</span>
        <span><span className="pm-dot dot-pend" /> Pendente</span>
        <span><span className="pm-dot dot-desat" /> Desatualizado</span>
        <span><span className="pm-dot dot-rej" /> Rejeitado</span>
      </div>
    </>
  )
}
