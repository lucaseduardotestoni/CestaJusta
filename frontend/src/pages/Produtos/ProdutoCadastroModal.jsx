import { useEffect, useState } from 'react'
import Modal from '../../components/Modal/Modal'
import Select from '../../components/Select/Select'
import { cadastrarProduto } from '../../services/api'
import { useToast } from '../../components/Toast/ToastContext'
import '../EnviarPreco/EnviarPrecoModal.css'

export default function ProdutoCadastroModal({ aberto, categorias, onFechar, onCadastrado }) {
  const { mostrarToast } = useToast()
  const [nome, setNome] = useState('')
  const [categoriaId, setCategoriaId] = useState('')
  const [marca, setMarca] = useState('')
  const [unidadeMedida, setUnidadeMedida] = useState('')
  const [codigoBarras, setCodigoBarras] = useState('')
  const [erro, setErro] = useState(null)
  const [enviando, setEnviando] = useState(false)

  useEffect(() => {
    if (!aberto) return
    setNome(''); setCategoriaId(''); setMarca(''); setUnidadeMedida(''); setCodigoBarras('')
    setErro(null); setEnviando(false)
  }, [aberto])

  async function enviar() {
    setErro(null)
    if (!nome.trim()) { setErro('Informe o nome do produto.'); return }
    if (nome.trim().length > 150) { setErro('Nome deve ter no máximo 150 caracteres.'); return }
    if (!categoriaId) { setErro('Selecione uma categoria.'); return }
    if (marca.length > 100) { setErro('Marca deve ter no máximo 100 caracteres.'); return }
    if (unidadeMedida.length > 30) { setErro('Unidade de medida deve ter no máximo 30 caracteres.'); return }
    if (codigoBarras.length > 50) { setErro('Código de barras deve ter no máximo 50 caracteres.'); return }

    setEnviando(true)
    try {
      await cadastrarProduto({
        nome: nome.trim(),
        categoriaId: Number(categoriaId),
        marca: marca.trim() || null,
        unidadeMedida: unidadeMedida.trim() || null,
        codigoBarras: codigoBarras.trim() || null,
      })
      mostrarToast('Produto cadastrado!')
      onCadastrado()
    } catch (e) {
      setErro(e.message)
    } finally {
      setEnviando(false)
    }
  }

  return (
    <Modal aberto={aberto} onFechar={onFechar} titulo="Cadastrar produto">
      <div className="ep-form">
        <label htmlFor="pc-nome">Nome</label>
        <input id="pc-nome" type="text" value={nome} disabled={enviando}
               onChange={e => setNome(e.target.value)} maxLength={150} />

        <label htmlFor="pc-categoria">Categoria</label>
        <Select id="pc-categoria" value={categoriaId} onChange={setCategoriaId} disabled={enviando}
                options={categorias.map(c => ({ value: String(c.id), label: c.nome }))}
                placeholder="Selecione…" />

        <label htmlFor="pc-marca">Marca</label>
        <input id="pc-marca" type="text" value={marca} disabled={enviando}
               onChange={e => setMarca(e.target.value)} maxLength={100} />

        <label htmlFor="pc-unidade">Unidade de medida</label>
        <input id="pc-unidade" type="text" value={unidadeMedida} disabled={enviando}
               placeholder="ex.: 5kg, 900ml" onChange={e => setUnidadeMedida(e.target.value)} maxLength={30} />

        <label htmlFor="pc-codigo">Código de barras</label>
        <input id="pc-codigo" type="text" value={codigoBarras} disabled={enviando}
               onChange={e => setCodigoBarras(e.target.value)} maxLength={50} />

        {erro && <div className="ep-erro">{erro}</div>}

        <div className="ep-acoes">
          <button type="button" className="ep-btn-enviar" onClick={enviar} disabled={enviando}>
            {enviando ? 'Cadastrando…' : 'Cadastrar'}
          </button>
          <button type="button" className="ep-btn-cancelar" onClick={onFechar} disabled={enviando}>
            Cancelar
          </button>
        </div>
      </div>
    </Modal>
  )
}