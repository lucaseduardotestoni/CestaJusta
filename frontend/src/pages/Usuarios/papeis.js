// Papéis de usuário e seus rótulos amigáveis (alinhados ao enum TipoUsuario do backend).
export const PAPEIS = [
  { value: 'CONSUMIDOR', label: 'Consumidor' },
  { value: 'COMERCIANTE', label: 'Comerciante' },
  { value: 'ADMIN', label: 'Administrador' },
]

export const ROTULO_PAPEL = Object.fromEntries(PAPEIS.map(p => [p.value, p.label]))

// Papéis que o cadastro público aceita (backend bloqueia criar ADMIN direto).
export const PAPEIS_CADASTRO = PAPEIS.filter(p => p.value !== 'ADMIN')