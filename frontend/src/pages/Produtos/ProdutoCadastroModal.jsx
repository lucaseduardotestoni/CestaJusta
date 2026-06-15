import { useEffect, useState } from 'react'
import Modal from '../../components/Modal/Modal'
import Select from '../../components/Select/Select'
import { cadastrarProduto, editarProduto } from '../../services/api'
import { useToast } from '../../components/Toast/ToastContext'
import { urlImagem } from './urlImagem'
import '../EnviarPreco/EnviarPrecoModal.css'

const MIME_OK = ['image/jpeg', 'image/png']
const TAMANHO_MAX = 5 * 1024 * 1024

export default function ProdutoCadastroModal({ aberto, categorias, produto, onFechar, onSalvo }) {
  const { mostrarToast } = useToast()
  const edicao = !!produto
  const [nome, setNome] = useState('')
  const [categoriaId, setCategoriaId] = useState('')
  const [marca, setMarca] = useState('')
  const [unidadeMedida, setUnidadeMedida] = useState('')
  const [codigoBarras, setCodigoBarras] = useState('')
  const [foto, setFoto] = useState(null)
  const [preview, setPreview] = useState(null)
  const [erro, setErro] = useState(null)
  const [enviando, setEnviando] = useState(false)

  useEffect(() => {
    if (!aberto) return
    setNome(produto?.nome || '')
    const catAtual = produto ? categorias.find(c => c.nome === produto.categoria) : null
    setCategoriaId(catAtual ? String(catAtual.id) : '')
    setMarca(produto?.marca || '')
    setUnidadeMedida(produto?.unidadeMedida || '')
    setCodigoBarras(produto?.codigoBarras || '')
    setFoto(null)
    setPreview(produto?.imagemPath ? urlImagem(produto.imagemPath) : null)
    setErro(null)
    setEnviando(false)
  }, [aberto, produto, categorias])

  function onArquivo(e) {
    const f = e.target.files?.[0]
    if (!f) return
    if (!MIME_OK.includes(f.type)) { setErro('Use uma imagem JPG ou PNG.'); return }
    if (f.size > TAMANHO_MAX) { setErro('Imagem excede 5MB.'); return }
    setErro(null)
    setFoto(f)
    setPreview(URL.createObjectURL(f))
  }

  async function salvar() {
    setErro(null)
    if (!nome.trim()) { setErro('Informe o nome do produto.'); return }
    if (nome.trim().length > 150) { setErro('Nome deve ter no máximo 150 caracteres.'); return }
    if (!categoriaId) { setErro('Selecione uma categoria.'); return }

    const dados = {
      nome: nome.trim(),
      categoriaId: Number(categoriaId),
      marca: marca.trim() || undefined,
      unidadeMedida: unidadeMedida.trim() || undefined,
      codigoBarras: codigoBarras.trim() || undefined,
      foto: foto || undefined,
    }
    setEnviando(true)
    try {
      if (edicao) await editarProduto(produto.id, dados)
      else await cadastrarProduto(dados)
      mostrarToast(edicao ? 'Produto atualizado!' : 'Produto cadastrado!')
      onSalvo()
    } catch (e) {
      setErro(e.message)
    } finally {
      setEnviando(false)
    }
  }

  return (
    <Modal aberto={aberto} onFechar={onFechar} titulo={edicao ? 'Editar produto' : 'Cadastrar produto'}>
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

        <label htmlFor="pc-foto">Imagem (JPG/PNG, até 5MB)</label>
        {preview && <img src={preview} alt="" className="pc-preview" />}
        <input id="pc-foto" type="file" accept="image/png,image/jpeg" disabled={enviando} onChange={onArquivo} />

        {erro && <div className="ep-erro">{erro}</div>}

        <div className="ep-acoes">
          <button type="button" className="ep-btn-enviar" onClick={salvar} disabled={enviando}>
            {enviando ? 'Salvando…' : 'Salvar'}
          </button>
          <button type="button" className="ep-btn-cancelar" onClick={onFechar} disabled={enviando}>
            Cancelar
          </button>
        </div>
      </div>
    </Modal>
  )
}