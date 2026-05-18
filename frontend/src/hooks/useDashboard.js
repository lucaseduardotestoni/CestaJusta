import { useEffect, useState } from 'react'
import { getDashboardKpis, getDashboardProdutos } from '../services/api'

export default function useDashboard() {
  const [kpis, setKpis] = useState(null)
  const [produtos, setProdutos] = useState([])
  const [totalProdutos, setTotalProdutos] = useState(0)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [ordem, setOrdem] = useState('todos')

  useEffect(() => {
    let cancelado = false
    setLoading(true)
    setError(null)

    Promise.all([
      getDashboardKpis(),
      getDashboardProdutos({ ordem, page: 0, size: 200 }),
    ])
      .then(([k, p]) => {
        if (cancelado) return
        setKpis(k)
        setProdutos(p.content || [])
        setTotalProdutos(p.totalElements ?? p.content?.length ?? 0)
      })
      .catch(err => { if (!cancelado) setError(err.message) })
      .finally(() => { if (!cancelado) setLoading(false) })

    return () => { cancelado = true }
  }, [ordem])

  return { kpis, produtos, totalProdutos, loading, error, ordem, setOrdem }
}