import { lazy, Suspense } from 'react'
import './Sparkline.css'

const SparklineInterno = lazy(() => import('./SparklineInterno'))

export default function Sparkline({ pontos, largura = 120, altura = 40, cor, comTooltip = false }) {
  if (!pontos || pontos.length === 0) {
    return <div className="sparkline-vazio" style={{ width: largura, height: altura }}>—</div>
  }
  return (
    <Suspense fallback={<div className="sparkline-skeleton" style={{ width: largura, height: altura }} />}>
      <SparklineInterno pontos={pontos} largura={largura} altura={altura} cor={cor} comTooltip={comTooltip} />
    </Suspense>
  )
}
