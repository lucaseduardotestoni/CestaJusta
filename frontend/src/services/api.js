const BASE_URL = '/api'

const USE_MOCKS = import.meta.env.VITE_USE_MOCKS === 'true'

let mocksModule = null
async function lazyMocks() {
  if (!mocksModule) mocksModule = await import('./mocks.js')
  return mocksModule
}

// Callback registrado pelo AuthContext: chamado quando a sessão morre de vez.
let onUnauthorized = null
export function setOnUnauthorized(fn) {
  onUnauthorized = fn
}

// Single-flight: 401s concorrentes compartilham UMA tentativa de refresh.
let refreshPromise = null
function refreshOnce() {
  if (!refreshPromise) {
    refreshPromise = fetch(`${BASE_URL}/refresh`, { method: 'POST' })
      .then(res => res.ok)
      .catch(() => false)
      .then(ok => { refreshPromise = null; return ok })
  }
  return refreshPromise
}

// Wrapper central: em 401, tenta refresh 1x e repete a request original.
async function apiFetch(path, opts = {}) {
  const { _noRefresh, _retried, ...fetchOpts } = opts
  const res = await fetch(`${BASE_URL}${path}`, fetchOpts)

  if (res.status === 401 && !_noRefresh && !_retried && path !== '/refresh') {
    const ok = await refreshOnce()
    if (ok) {
      return apiFetch(path, { ...opts, _retried: true })
    }
    if (onUnauthorized) onUnauthorized()
  }
  return handleResponse(res)
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
  // _noRefresh: 401 aqui = credencial errada, não sessão expirada.
  await apiFetch('/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email, senha }),
    _noRefresh: true,
  })
}

export async function getMe() {
  return apiFetch('/usuarios/me')
}

export async function logout() {
  return apiFetch('/logout', { method: 'POST', _noRefresh: true })
}

export async function cadastrar(nome, email, senha, tipoUsuario) {
  return apiFetch('/usuarios/cadastro', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ nome, email, senha, tipoUsuario }),
    _noRefresh: true,
  })
}

export async function getProdutos() {
  return apiFetch('/produtos')
}

export async function getDashboardKpis() {
  if (USE_MOCKS) return (await lazyMocks()).getDashboardKpis()
  return apiFetch('/dashboard/kpis')
}

export async function getDashboardProdutos(opts) {
  if (USE_MOCKS) return (await lazyMocks()).getDashboardProdutos(opts)
  const { ordem = 'todos', page = 0, size = 20 } = opts || {}
  const params = new URLSearchParams({ ordem, page, size })
  return apiFetch(`/dashboard/produtos?${params}`)
}

export async function getHistoricoProduto(produtoId, dias = 30) {
  if (USE_MOCKS) return (await lazyMocks()).getHistoricoProduto(produtoId, dias)
  return apiFetch(`/comparacoes/produto/${produtoId}/historico?dias=${dias}`)
}

export async function getPrecosPorProduto(produtoId) {
  return apiFetch(`/precos/produto/${produtoId}`)
}

export async function getMeusPrecosDenunciados() {
  return apiFetch('/denuncias/meus-precos')
}

export async function getMinhasDenuncias() {
  return apiFetch('/denuncias/minhas')
}

export async function getDenuncias(status) {
  const qs = status ? `?status=${status}` : ''
  return apiFetch(`/denuncias${qs}`)
}

export async function votarDenuncia(id, tipo) {
  return apiFetch(`/denuncias/${id}/votos`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ tipo }),
  })
}

export async function retirarVotoDenuncia(id) {
  return apiFetch(`/denuncias/${id}/votos`, { method: 'DELETE' })
}

export async function cancelarDenuncia(id) {
  return apiFetch(`/denuncias/${id}/cancelar`, { method: 'PATCH' })
}

export async function resolverDenuncia(id, status) {
  return apiFetch(`/denuncias/${id}/resolver?status=${status}`, { method: 'PUT' })
}

export async function confirmarPreco(precoId) {
  return apiFetch(`/precos/${precoId}/confirmacoes`, { method: 'POST' })
}

export async function denunciarPreco({ precoId, motivo, descricao, foto }) {
  const form = new FormData()
  form.append('precoId', precoId)
  form.append('motivo', motivo)
  if (descricao) form.append('descricao', descricao)
  if (foto) form.append('foto', foto)
  // NÃO definir Content-Type: o browser define o boundary do multipart.
  return apiFetch('/denuncias', { method: 'POST', body: form })
}

export async function getMercados() {
  return apiFetch('/mercados')
}

export async function getMercadosAdmin() {
  return apiFetch('/mercados/todos')
}

export async function cadastrarMercado(dados) {
  return apiFetch('/mercados/cadastro', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(dados),
  })
}

export async function editarMercado(id, dados) {
  return apiFetch(`/mercados/${id}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(dados),
  })
}

export async function inativarMercado(id) {
  return apiFetch(`/mercados/${id}`, { method: 'DELETE' })
}

export async function ativarMercado(id) {
  return apiFetch(`/mercados/${id}/ativar`, { method: 'PATCH' })
}

export async function cadastrarPreco({ produtoId, mercadoId, valor, dataColeta }) {
  return apiFetch('/precos/cadastro', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ produtoId, mercadoId, valor, dataColeta }),
  })
}

export async function getCategorias() {
  return apiFetch('/categorias')
}

export async function getProdutosAdmin() {
  return apiFetch('/produtos/todos')
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
  return apiFetch('/produtos/cadastro', { method: 'POST', body: formProduto(dados) })
}

export async function editarProduto(id, dados) {
  return apiFetch(`/produtos/${id}`, { method: 'PUT', body: formProduto(dados) })
}

export async function inativarProduto(id) {
  return apiFetch(`/produtos/${id}`, { method: 'DELETE' })
}

export async function ativarProduto(id) {
  return apiFetch(`/produtos/${id}/ativar`, { method: 'PATCH' })
}
