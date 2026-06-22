import { useState } from 'react'
import { semanaAtual, rotuloSemana } from './semana'
import './ResumoSemana.css'

export default function ResumoSemana() {
  // Calculado uma vez na montagem; "Atualizado em" usa o horário de carregamento (cliente).
  const [info] = useState(() => {
    const { inicio, fim } = semanaAtual()
    return {
      rotulo: rotuloSemana(inicio, fim),
      atualizadoEm: new Date().toLocaleTimeString('pt-BR', { hour: '2-digit', minute: '2-digit' }),
    }
  })

  return (
    <section className="resumo-semana">
      <span className="resumo-semana-data">
        <span aria-hidden="true">📅</span> {info.rotulo}
      </span>
      <span className="resumo-semana-atualizado">Atualizado em {info.atualizadoEm}</span>
    </section>
  )
}
