import { useEffect, useId, useLayoutEffect, useRef, useState } from 'react'
import { createPortal } from 'react-dom'
import './Select.css'

/**
 * Select custom (listbox) — substituto estilizável do <select> nativo.
 * options: [{ value, label }]. value/onChange controlam a seleção.
 * A lista é renderizada via portal (escapa overflow/stacking do modal) e
 * posicionada nas coordenadas do controle.
 */
export default function Select({ value, onChange, options, placeholder = 'Selecione…', disabled = false, id }) {
  const [aberto, setAberto] = useState(false)
  const [destacado, setDestacado] = useState(-1)
  const [coords, setCoords] = useState(null)
  const controleRef = useRef(null)
  const listaRef = useRef(null)
  const autoId = useId()
  const listId = `${id || autoId}-lista`

  const selecionada = options.find(o => o.value === value)

  function abrir() {
    if (disabled) return
    const r = controleRef.current.getBoundingClientRect()
    setCoords({ top: r.bottom + 4, left: r.left, width: r.width })
    const idx = options.findIndex(o => o.value === value)
    setDestacado(idx >= 0 ? idx : 0)
    setAberto(true)
  }

  function escolher(opt) {
    onChange(opt.value)
    setAberto(false)
    controleRef.current?.focus()
  }

  useEffect(() => {
    if (!aberto) return
    function onDown(e) {
      if (controleRef.current?.contains(e.target)) return
      if (listaRef.current?.contains(e.target)) return
      setAberto(false)
    }
    function fechar() { setAberto(false) }
    function onScroll(e) {
      // rolar dentro da própria lista é permitido; só fecha se a página/modal atrás rolar
      if (listaRef.current?.contains(e.target)) return
      setAberto(false)
    }
    document.addEventListener('mousedown', onDown)
    window.addEventListener('resize', fechar)
    window.addEventListener('scroll', onScroll, true)
    return () => {
      document.removeEventListener('mousedown', onDown)
      window.removeEventListener('resize', fechar)
      window.removeEventListener('scroll', onScroll, true)
    }
  }, [aberto])

  useLayoutEffect(() => {
    if (!aberto || destacado < 0 || !listaRef.current) return
    listaRef.current.children[destacado]?.scrollIntoView({ block: 'nearest' })
  }, [aberto, destacado])

  function onKeyDown(e) {
    if (disabled) return
    if (!aberto) {
      if (e.key === 'ArrowDown' || e.key === 'Enter' || e.key === ' ') { e.preventDefault(); abrir() }
      return
    }
    if (e.key === 'Escape') { e.preventDefault(); setAberto(false) }
    else if (e.key === 'ArrowDown') { e.preventDefault(); setDestacado(i => Math.min(options.length - 1, i + 1)) }
    else if (e.key === 'ArrowUp') { e.preventDefault(); setDestacado(i => Math.max(0, i - 1)) }
    else if (e.key === 'Enter') { e.preventDefault(); if (options[destacado]) escolher(options[destacado]) }
    else if (e.key === 'Tab') { setAberto(false) }
  }

  return (
    <div className="sel">
      <button type="button" ref={controleRef} id={id}
              className={`sel-controle ${aberto ? 'aberto' : ''}`}
              disabled={disabled}
              role="combobox" aria-haspopup="listbox" aria-expanded={aberto} aria-controls={listId}
              aria-activedescendant={aberto && destacado >= 0 ? `${listId}-${destacado}` : undefined}
              onClick={() => (aberto ? setAberto(false) : abrir())}
              onKeyDown={onKeyDown}>
        <span className={selecionada ? 'sel-valor' : 'sel-placeholder'}>
          {selecionada ? selecionada.label : placeholder}
        </span>
        <span className="sel-seta" aria-hidden="true" />
      </button>

      {aberto && coords && createPortal(
        <ul ref={listaRef} id={listId} role="listbox" className="sel-lista"
            style={{ top: coords.top, left: coords.left, width: coords.width }}>
          {options.map((o, i) => (
            <li key={o.value} id={`${listId}-${i}`} role="option" aria-selected={o.value === value}
                className={`sel-opcao ${i === destacado ? 'destacado' : ''} ${o.value === value ? 'selecionada' : ''}`}
                onMouseEnter={() => setDestacado(i)}
                onMouseDown={(e) => { e.preventDefault(); escolher(o) }}>
              {o.label}
            </li>
          ))}
        </ul>,
        document.body
      )}
    </div>
  )
}