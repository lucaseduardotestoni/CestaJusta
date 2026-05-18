const HOJE = new Date()

function diasAtras(n) {
  const d = new Date(HOJE)
  d.setDate(d.getDate() - n)
  return d.toISOString().slice(0, 10)
}

function fakeSparkline(base = 5) {
  return Array.from({ length: 30 }, (_, i) => ({
    data: diasAtras(29 - i),
    valor: +(base + Math.sin(i / 4) * 0.5).toFixed(2),
  }))
}

export async function getDashboardKpis() {
  return {
    valorCesta: 32.18,
    variacaoSemanal: -7.5,
    totalProdutos: 1342,
    totalMercados: 24,
    economiaMedia: 27.0,
  }
}

export async function getDashboardProdutos(opts) {
  const { ordem = 'todos', page = 0, size = 20 } = opts || {}
  const produtosBase = [
    { id: 1, nome: 'Leite Italac Integral 1L', marca: 'Italac', unidadeMedida: '1L',
      categoria: 'Laticínios', imagemPath: 'https://images.unsplash.com/photo-1563636619-e9143da7973b?w=200',
      menorPreco: 4.79, mercadoMenorNome: 'Mercado Verde', mercadoMenorId: 1,
      tendenciaPercentual: -8, sparkline: fakeSparkline(5) },
    { id: 2, nome: 'Arroz Tio João 5kg', marca: 'Tio João', unidadeMedida: '5kg',
      categoria: 'Grãos', imagemPath: 'https://images.unsplash.com/photo-1568347877321-f8935c7dc5a8?w=200',
      menorPreco: 28.10, mercadoMenorNome: 'Cooper Vale', mercadoMenorId: 2,
      tendenciaPercentual: 3, sparkline: fakeSparkline(28) },
    { id: 3, nome: 'Banana Prata 1kg', marca: '', unidadeMedida: '1kg',
      categoria: 'Hortifruti', imagemPath: 'https://images.unsplash.com/photo-1599909533734-7e7e2e07c1d6?w=200',
      menorPreco: 6.49, mercadoMenorNome: 'Bistek Velha', mercadoMenorId: 3,
      tendenciaPercentual: -2, sparkline: fakeSparkline(6) },
  ]

  let ordenado = [...produtosBase]
  if (ordem === 'quedas') ordenado.sort((a, b) => a.tendenciaPercentual - b.tendenciaPercentual)
  if (ordem === 'altas')  ordenado.sort((a, b) => b.tendenciaPercentual - a.tendenciaPercentual)

  const start = page * size
  const content = ordenado.slice(start, start + size)

  return {
    content,
    totalElements: ordenado.length,
    totalPages: Math.ceil(ordenado.length / size),
    number: page,
    size,
  }
}

export async function getHistoricoProduto(produtoId, _dias = 30) {
  return {
    produtoId,
    produtoNome: 'Produto mock',
    pontos: fakeSparkline(5),
  }
}
