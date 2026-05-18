import { LineChart, Line, ResponsiveContainer, YAxis, Tooltip } from 'recharts'

export default function SparklineInterno({ pontos, largura, altura, cor, comTooltip }) {
  const valores = pontos.map(p => p.valor)
  const min = Math.min(...valores)
  const max = Math.max(...valores)
  const corLinha = cor || (pontos[0].valor > pontos[pontos.length - 1].valor
    ? 'var(--cor-success)' : 'var(--cor-danger)')

  return (
    <div style={{ width: largura, height: altura }}>
      <ResponsiveContainer>
        <LineChart data={pontos} margin={{ top: 2, right: 2, bottom: 2, left: 2 }}>
          <YAxis hide domain={[min - 0.5, max + 0.5]} />
          {comTooltip && (
            <Tooltip
              content={<TooltipPersonalizado />}
              cursor={{ stroke: 'var(--cor-border)', strokeWidth: 1, strokeDasharray: '3 3' }}
            />
          )}
          <Line
            type="monotone"
            dataKey="valor"
            stroke={corLinha}
            strokeWidth={2}
            dot={false}
            activeDot={comTooltip ? { r: 4, strokeWidth: 0, fill: corLinha } : false}
            isAnimationActive={false}
          />
        </LineChart>
      </ResponsiveContainer>
    </div>
  )
}

function TooltipPersonalizado({ active, payload }) {
  if (!active || !payload || !payload.length) return null
  const ponto = payload[0].payload
  const valor = new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(ponto.valor)
  return (
    <div className="sparkline-tooltip">
      <div className="sparkline-tooltip-data">{formatarData(ponto.data)}</div>
      <div className="sparkline-tooltip-valor">{valor}</div>
    </div>
  )
}

function formatarData(d) {
  if (!d) return ''
  const [ano, mes, dia] = String(d).split('-')
  return `${dia}/${mes}/${ano}`
}