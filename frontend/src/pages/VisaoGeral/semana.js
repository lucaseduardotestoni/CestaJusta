// Intervalo da semana atual (segunda a domingo) e rótulo pt-BR. Cálculo client-side.
const MESES = ['jan', 'fev', 'mar', 'abr', 'mai', 'jun', 'jul', 'ago', 'set', 'out', 'nov', 'dez']

export function semanaAtual(hoje = new Date()) {
  const base = new Date(hoje)
  base.setHours(0, 0, 0, 0)
  const dia = base.getDay() // 0=dom, 1=seg, ... 6=sab
  const ateSegunda = dia === 0 ? -6 : 1 - dia
  const inicio = new Date(base)
  inicio.setDate(base.getDate() + ateSegunda)
  const fim = new Date(inicio)
  fim.setDate(inicio.getDate() + 6)
  return { inicio, fim }
}

export function rotuloSemana(inicio, fim) {
  const mesI = MESES[inicio.getMonth()]
  const mesF = MESES[fim.getMonth()]
  if (mesI === mesF) {
    return `Semana de ${inicio.getDate()} a ${fim.getDate()} de ${mesF}`
  }
  return `Semana de ${inicio.getDate()} de ${mesI} a ${fim.getDate()} de ${mesF}`
}
