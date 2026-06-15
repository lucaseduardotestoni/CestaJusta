const BASE_URL = '/api'

const USE_MOCKS = import.meta.env.VITE_USE_MOCKS === 'true'

let mocksModule = null
async function lazyMocks() {
  if (!mocksModule) mocksModule = await import('./mocks.js')
  return mocksModule
}

function authHeaders() {
  const token = localStorage.getItem('token')
  return token ? { Authorization: `Bearer ${token}` } : {}
}

async function handleResponse(res) {
  const text = await res.text()

  let parsed
  try {
    parsed = JSON.parse(text)
  } catch {
    parsed = text
  }

  if (!res.ok) {
    const message =
      (typeof parsed === 'object' && parsed?.message) ||
      (typeof parsed === 'string' && parsed) ||
      'Erro desconhecido'
    throw new Error(message)
  }

  return parsed
}

export async function login(email, senha) {
  const res = await fetch(`${BASE_URL}/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email, senha }),
  })
  return handleResponse(res) // retorna o JWT como string
}

export async function getProdutos() {
  const res = await fetch(`${BASE_URL}/produtos`, {
    headers: { ...authHeaders() },
  })
  return handleResponse(res)
}

export async function cadastrar(nome, email, senha, tipoUsuario) {
  const res = await fetch(`${BASE_URL}/usuarios/cadastro`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ nome, email, senha, tipoUsuario }),
  })
  return handleResponse(res) // retorna UsuarioResponseDTO
}

export async function getDashboardKpis() {
  if (USE_MOCKS) return (await lazyMocks()).getDashboardKpis()
  const res = await fetch(`${BASE_URL}/dashboard/kpis`, {
    headers: { ...authHeaders() },
  })
  return handleResponse(res)
}

export async function getDashboardProdutos(opts) {
  if (USE_MOCKS) return (await lazyMocks()).getDashboardProdutos(opts)
  const { ordem = 'todos', page = 0, size = 20 } = opts || {}
  const params = new URLSearchParams({ ordem, page, size })
  const res = await fetch(`${BASE_URL}/dashboard/produtos?${params}`, {
    headers: { ...authHeaders() },
  })
  return handleResponse(res)
}

export async function getHistoricoProduto(produtoId, dias = 30) {
  if (USE_MOCKS) return (await lazyMocks()).getHistoricoProduto(produtoId, dias)
  const res = await fetch(`${BASE_URL}/comparacoes/produto/${produtoId}/historico?dias=${dias}`, {
    headers: { ...authHeaders() },
  })
  return handleResponse(res)
}

export async function getPrecosPorProduto(produtoId) {
  const res = await fetch(`${BASE_URL}/precos/produto/${produtoId}`, {
    headers: { ...authHeaders() },
  })
  return handleResponse(res)
}

export async function getMeusPrecosDenunciados() {
  const res = await fetch(`${BASE_URL}/denuncias/meus-precos`, {
    headers: { ...authHeaders() },
  })
  return handleResponse(res) // retorna number[] (ids de preço já denunciados na janela)
}

export async function confirmarPreco(precoId) {
  const res = await fetch(`${BASE_URL}/precos/${precoId}/confirmacoes`, {
    method: 'POST',
    headers: { ...authHeaders() },
  })
  return handleResponse(res) // 204 → corpo vazio
}

export async function denunciarPreco({ precoId, motivo, descricao, foto }) {
  const form = new FormData()
  form.append('precoId', precoId)
  form.append('motivo', motivo)
  if (descricao) form.append('descricao', descricao)
  if (foto) form.append('foto', foto)
  // NÃO definir Content-Type manualmente: o browser define o boundary do multipart.
  const res = await fetch(`${BASE_URL}/denuncias`, {
    method: 'POST',
    headers: { ...authHeaders() },
    body: form,
  })
  return handleResponse(res) // 201 → corpo vazio
}

export async function getMercados() {
  const res = await fetch(`${BASE_URL}/mercados`, {
    headers: { ...authHeaders() },
  })
  return handleResponse(res)
}

export async function cadastrarPreco({ produtoId, mercadoId, valor, dataColeta }) {
  const res = await fetch(`${BASE_URL}/precos/cadastro`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json', ...authHeaders() },
    body: JSON.stringify({ produtoId, mercadoId, valor, dataColeta }),
  })
  return handleResponse(res) // PrecoResponseDTO (201)
}

export async function getCategorias() {
  const res = await fetch(`${BASE_URL}/categorias`, { headers: { ...authHeaders() } })
  return handleResponse(res) // [{ id, nome }]
}

export async function getProdutosAdmin() {
  const res = await fetch(`${BASE_URL}/produtos/todos`, { headers: { ...authHeaders() } })
  return handleResponse(res) // ProdutoResponseDTO[] incluindo inativos
}

function formProduto({ nome, codigoBarras, marca, unidadeMedida, categoriaId, foto }) {
  const form = new FormData()
  form.append('nome', nome)
  form.append('categoriaId', categoriaId)
  if (marca) form.append('marca', marca)
  if (unidadeMedida) form.append('unidadeMedida', unidadeMedida)
  if (codigoBarras) form.append('codigoBarras', codigoBarras)
  if (foto) form.append('foto', foto)
  return form
}

export async function cadastrarProduto(dados) {
  // NÃO definir Content-Type: o browser define o boundary do multipart.
  const res = await fetch(`${BASE_URL}/produtos/cadastro`, {
    method: 'POST',
    headers: { ...authHeaders() },
    body: formProduto(dados),
  })
  return handleResponse(res) // 201 ProdutoResponseDTO
}

export async function editarProduto(id, dados) {
  const res = await fetch(`${BASE_URL}/produtos/${id}`, {
    method: 'PUT',
    headers: { ...authHeaders() },
    body: formProduto(dados),
  })
  return handleResponse(res) // ProdutoResponseDTO
}

export async function inativarProduto(id) {
  const res = await fetch(`${BASE_URL}/produtos/${id}`, {
    method: 'DELETE',
    headers: { ...authHeaders() },
  })
  return handleResponse(res) // ProdutoResponseDTO
}

export async function ativarProduto(id) {
  const res = await fetch(`${BASE_URL}/produtos/${id}/ativar`, {
    method: 'PATCH',
    headers: { ...authHeaders() },
  })
  return handleResponse(res) // ProdutoResponseDTO
}
