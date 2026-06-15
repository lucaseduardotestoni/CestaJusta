// Resolve o caminho de imagem para uma URL utilizável no <img src>.
// Caminhos absolutos (ex.: "/produtos/arroz.png", assets estáticos do front) ficam como estão;
// caminhos relativos do storage (ex.: "produtos/uuid.jpg", servidos pelo backend) ganham o prefixo /uploads.
export function urlImagem(path) {
  if (!path) return null
  return path.startsWith('/') ? path : `/uploads/${path}`
}