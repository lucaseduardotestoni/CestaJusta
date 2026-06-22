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
        <img className="resumo-semana-icone" src="/cal-semana.webp" alt="" />
        {info.rotulo}
      </span>
      <span className="resumo-semana-atualizado">
        <svg className="resumo-semana-relogio" width="13" height="13" viewBox="0 0 24 24"
             fill="none" stroke="currentColor" strokeWidth="2.2" strokeLinecap="round" aria-hidden="true">
          <circle cx="12" cy="12" r="9" />
          <path d="M12 7v5l3 2" />
        </svg>
        <span className="resumo-semana-prefixo">Atualizado em </span>{info.atualizadoEm}
      </span>
    </section>
  )
}
