import { useEffect, useState } from 'react'
import Modal from '../../components/Modal/Modal'
import Select from '../../components/Select/Select'
import { getProdutos, getMercados, cadastrarPreco } from '../../services/api'
import { useToast } from '../../components/Toast/ToastContext'
import { useDashboardRefresh } from '../../context/DashboardRefreshContext'
import './EnviarPrecoModal.css'

function hojeISO() {
  return new Date().toISOString().slice(0, 10)
}

// Backend: BigDecimal(precision=10, scale=2) -> no máximo 8 inteiros + 2 centavos (R$ 99.999.999,99).
const MAX_DIGITS = 10

// Dígitos digitados são tratados como centavos: "999" -> "9.99" (decimal exato, sem float).
// Limita a MAX_DIGITS para não estourar a precisão do banco; dígitos extras são ignorados.
function digitsParaDecimal(texto) {
  const digits = (texto || '').replace(/\D/g, '').slice(0, MAX_DIGITS)
  if (!digits) return ''
  const padded = digits.padStart(3, '0')
  return `${padded.slice(0, -2)}.${padded.slice(-2)}`
}

// Mesma entrada formatada como moeda BRL para exibição: "999" -> "R$ 9,99".
function formatarMoeda(texto) {
  const dec = digitsParaDecimal(texto)
  if (!dec) return ''
  return Number(dec).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })
}

export default function EnviarPrecoModal({ aberto, onFechar }) {
  const [produtos, setProdutos] = useState([])
  const [mercados, setMercados] = useState([])
  const [carregando, setCarregando] = useState(false)
  const [erroCarregar, setErroCarregar] = useState(null)

  const [produtoId, setProdutoId] = useState('')
  const [mercadoId, setMercadoId] = useState('')
  const [valor, setValor] = useState('')
  const [dataColeta, setDataColeta] = useState(hojeISO())
  const [erro, setErro] = useState(null)
  const [enviando, setEnviando] = useState(false)

  const { mostrarToast } = useToast()
  const { solicitarRefresh } = useDashboardRefresh()

  useEffect(() => {
    if (!aberto) return
    setProdutoId(''); setMercadoId(''); setValor(''); setDataColeta(hojeISO())
    setErro(null); setErroCarregar(null); setEnviando(false)
    setCarregando(true)
    let cancelado = false
    Promise.all([getProdutos(), getMercados()])
      .then(([prods, mercs]) => {
        if (cancelado) return
        setProdutos(prods.slice().sort((a, b) => a.nome.localeCompare(b.nome)))
        setMercados(mercs.slice().sort((a, b) => a.nomeFantasia.localeCompare(b.nomeFantasia)))
      })
      .catch(e => { if (!cancelado) setErroCarregar(e.message) })
      .finally(() => { if (!cancelado) setCarregando(false) })
    return () => { cancelado = true }
  }, [aberto])

  async function enviar() {
    setErro(null)
    const valorEnvio = digitsParaDecimal(valor)
    if (!produtoId) { setErro('Selecione um produto.'); return }
    if (!mercadoId) { setErro('Selecione um mercado.'); return }
    if (!valorEnvio || Number(valorEnvio) <= 0) { setErro('Informe um valor maior que zero.'); return }
    if (!dataColeta) { setErro('Informe a data da coleta.'); return }
    if (dataColeta > hojeISO()) { setErro('A data de coleta não pode ser futura.'); return }

    setEnviando(true)
    try {
      await cadastrarPreco({
        produtoId: Number(produtoId),
        mercadoId: Number(mercadoId),
        valor: valorEnvio,
        dataColeta,
      })
      onFechar()
      mostrarToast('Preço enviado com sucesso!')
      solicitarRefresh()
    } catch (e) {
      setErro(e.message)
      setEnviando(false)
    }
  }

  return (
    <Modal aberto={aberto} onFechar={onFechar} titulo="Enviar preço">
      {carregando && <p>Carregando...</p>}
      {erroCarregar && (
        <p className="ep-erro">Não foi possível carregar produtos/mercados: {erroCarregar}</p>
      )}

      {!carregando && !erroCarregar && (
        <div className="ep-form">
          <label htmlFor="ep-produto">Produto</label>
          <Select id="ep-produto" value={produtoId} onChange={setProdutoId} disabled={enviando}
                  options={produtos.map(p => ({ value: String(p.id), label: p.nome }))} />

          <label htmlFor="ep-mercado">Mercado</label>
          <Select id="ep-mercado" value={mercadoId} onChange={setMercadoId} disabled={enviando}
                  options={mercados.map(m => ({ value: String(m.id), label: m.nomeFantasia }))} />

          <label htmlFor="ep-valor">Valor (R$)</label>
          <input id="ep-valor" type="text" inputMode="numeric" value={valor}
                 placeholder="R$ 0,00" onChange={e => setValor(formatarMoeda(e.target.value))}
                 disabled={enviando} />

          <label htmlFor="ep-data">Data da coleta</label>
          <input id="ep-data" type="date" value={dataColeta} max={hojeISO()}
                 onChange={e => setDataColeta(e.target.value)} disabled={enviando} />

          {erro && <div className="ep-erro">{erro}</div>}

          <div className="ep-acoes">
            <button type="button" className="ep-btn-enviar" onClick={enviar} disabled={enviando}>
              {enviando ? 'Enviando…' : 'Enviar preço'}
            </button>
            <button type="button" className="ep-btn-cancelar" onClick={onFechar} disabled={enviando}>
              Cancelar
            </button>
          </div>
        </div>
      )}
    </Modal>
  )
}
