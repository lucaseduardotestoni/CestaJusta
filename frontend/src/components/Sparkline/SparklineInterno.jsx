import { LineChart, Line, ResponsiveContainer, YAxis } from 'recharts'

export default function SparklineInterno({ pontos, largura, altura, cor }) {
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
          <Line
            type="monotone"
            dataKey="valor"
            stroke={corLinha}
            strokeWidth={2}
            dot={false}
            isAnimationActive={false}
          />
        </LineChart>
      </ResponsiveContainer>
    </div>
  )
}
