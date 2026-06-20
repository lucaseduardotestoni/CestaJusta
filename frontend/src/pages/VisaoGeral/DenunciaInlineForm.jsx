import { useEffect, useRef, useState } from 'react'
import { denunciarPreco } from '../../services/api'

const MOTIVOS = [
  'Preço está errado',
  'Preço desatualizado',
  'Preço abusivo / fora da realidade',
  'Produto não corresponde',
]
const MAX_FOTO_BYTES = 5 * 1024 * 1024
const TIPOS_OK = ['image/jpeg', 'image/png', 'image/webp']
const moeda = new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' })

export default function DenunciaInlineForm({ preco, onCancelar, onSucesso }) {
  const [motivo, setMotivo] = useState(MOTIVOS[0])
  const [descricao, setDescricao] = useState('')
  const [foto, setFoto] = useState(null)
  const [previewUrl, setPreviewUrl] = useState(null)
  const [erro, setErro] = useState(null)
  const [enviando, setEnviando] = useState(false)
  const inputFileRef = useRef(null)

  useEffect(() => {
    return () => { if (previewUrl) URL.revokeObjectURL(previewUrl) }
  }, [previewUrl])

  function aoEscolherFoto(e) {
    setErro(null)
    const arquivo = e.target.files?.[0]
    if (!arquivo) return
    if (!TIPOS_OK.includes(arquivo.type)) {
      setErro('Formato não suportado. Use JPG, PNG ou WEBP.')
      return
    }
    if (arquivo.size > MAX_FOTO_BYTES) {
      setErro('A imagem excede 5 MB.')
      return
    }
    if (previewUrl) URL.revokeObjectURL(previewUrl)
    setFoto(arquivo)
    setPreviewUrl(URL.createObjectURL(arquivo))
  }

  function removerFoto() {
    if (previewUrl) URL.revokeObjectURL(previewUrl)
    setFoto(null)
    setPreviewUrl(null)
    if (inputFileRef.current) inputFileRef.current.value = ''
  }

  async function enviar() {
    setEnviando(true)
    setErro(null)
    try {
      await denunciarPreco({ precoId: preco.id, motivo, descricao: descricao.trim(), foto })
      onSucesso()
    } catch (e) {
      setErro(e.message)
      setEnviando(false)
    }
  }

  return (
    <div className="pm-form-den">
      <h4>Denunciar preço</h4>
      <div className="pm-form-alvo">
        {preco.mercadoNomeFantasia} · {moeda.format(preco.valor)}
      </div>

      <label htmlFor={`motivo-${preco.id}`}>Motivo</label>
      <select id={`motivo-${preco.id}`} value={motivo}
              onChange={e => setMotivo(e.target.value)} disabled={enviando}>
        {MOTIVOS.map(m => <option key={m} value={m}>{m}</option>)}
      </select>

      <label htmlFor={`desc-${preco.id}`}>Descrição (opcional)</label>
      <textarea id={`desc-${preco.id}`} maxLength={1000} value={descricao}
                placeholder="Conte mais sobre o problema…"
                onChange={e => setDescricao(e.target.value)} disabled={enviando} />
      <div className="pm-contador">{descricao.length} / 1000</div>

      <label htmlFor={`foto-${preco.id}`}>Foto (opcional)</label>
      <label htmlFor={`foto-${preco.id}`} className={`pm-foto-drop ${enviando ? 'desabilitado' : ''}`}>
        {previewUrl
          ? <img src={previewUrl} alt="" className="pm-foto-thumb" />
          : (
            <span className="pm-foto-icone" aria-hidden="true">
              <svg width="36" height="36" viewBox="0 0 24 24" fill="none"
                   stroke="currentColor" strokeWidth="1.6" strokeLinecap="round" strokeLinejoin="round">
                <rect x="3" y="4" width="18" height="14" rx="2" />
                <circle cx="8.5" cy="9" r="1.5" />
                <path d="M21 15l-5-5L5 18" />
                <path d="M12 20v-3m0 0l-1.5 1.5M12 17l1.5 1.5" />
              </svg>
            </span>
          )}
        <span className="pm-foto-texto">{previewUrl ? 'Trocar imagem' : 'Escolher imagem'}</span>
        <span className="pm-foto-hint">JPG, PNG ou WEBP · até 5 MB</span>
      </label>
      <input id={`foto-${preco.id}`} ref={inputFileRef} type="file" className="pm-foto-input"
             accept="image/jpeg,image/png,image/webp"
             onChange={aoEscolherFoto} disabled={enviando} />
      {previewUrl && (
        <div className="pm-foto-preview">
          <button type="button" className="pm-foto-remover" onClick={removerFoto}
                  disabled={enviando}>Remover foto</button>
        </div>
      )}

      {erro && <div className="pm-erro">{erro}</div>}

      <div className="pm-form-acoes">
        <button type="button" className="pm-btn-enviar" onClick={enviar} disabled={enviando}>
          {enviando ? 'Enviando…' : 'Enviar denúncia'}
        </button>
        <button type="button" className="pm-btn-cancelar" onClick={onCancelar} disabled={enviando}>
          Cancelar
        </button>
      </div>
      <div className="pm-nota">O backend valida 1 denúncia por preço a cada 3 dias.
        A comunidade vota (3 confirmações reprovam o preço).</div>
    </div>
  )
}
