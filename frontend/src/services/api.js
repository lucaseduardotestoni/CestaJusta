const BASE_URL = '/api'

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

export async function getComparacaoProduto(produtoId) {
  const res = await fetch(`${BASE_URL}/comparacoes/produto/${produtoId}`, {
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
