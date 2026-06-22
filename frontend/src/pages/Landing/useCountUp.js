import { useLayoutEffect } from 'react'

/*
 * Count-up dos números da landing (Fase 2 do redesign).
 * Anima cada elemento `.landing .count` de 0 até `data-target` quando ele
 * entra na viewport (uma vez). Formatação em pt-BR via data-prefix /
 * data-suffix / data-decimals. Respeita prefers-reduced-motion.
 *
 * Os spans já vêm renderizados com o valor final (bom sem JS); o hook só
 * adiciona a animação por cima.
 */
export default function useCountUp() {
  useLayoutEffect(() => {
    const reduce = window.matchMedia('(prefers-reduced-motion: reduce)').matches
    const els = Array.from(document.querySelectorAll('.landing .count'))
    if (els.length === 0) return

    const fmt = (value, dec, prefix, suffix) =>
      (prefix || '') +
      value.toLocaleString('pt-BR', {
        minimumFractionDigits: dec,
        maximumFractionDigits: dec,
      }) +
      (suffix || '')

    const finalText = (el) => {
      const target = parseFloat(el.dataset.target)
      const dec = parseInt(el.dataset.decimals || '0', 10)
      return fmt(target, dec, el.dataset.prefix, el.dataset.suffix)
    }

    // Reduzir movimento: mostra o valor final, sem animar.
    if (reduce) {
      els.forEach((el) => { el.textContent = finalText(el) })
      return
    }

    // Estado inicial = 0 (antes do paint, sem flash do valor final).
    els.forEach((el) => {
      const dec = parseInt(el.dataset.decimals || '0', 10)
      el.textContent = fmt(0, dec, el.dataset.prefix, el.dataset.suffix)
    })

    const animate = (el) => {
      const target = parseFloat(el.dataset.target)
      const dec = parseInt(el.dataset.decimals || '0', 10)
      const prefix = el.dataset.prefix
      const suffix = el.dataset.suffix
      const duration = 1400
      let startTs = null
      const step = (ts) => {
        if (startTs === null) startTs = ts
        const t = Math.min(1, (ts - startTs) / duration)
        const eased = 1 - Math.pow(1 - t, 3) // easeOutCubic
        el.textContent = fmt(target * eased, dec, prefix, suffix)
        if (t < 1) requestAnimationFrame(step)
        else el.textContent = finalText(el)
      }
      requestAnimationFrame(step)
    }

    const io = new IntersectionObserver(
      (entries) => {
        entries.forEach((entry) => {
          if (entry.isIntersecting) {
            animate(entry.target)
            io.unobserve(entry.target)
          }
        })
      },
      { threshold: 0.4 },
    )

    els.forEach((el) => io.observe(el))
    return () => io.disconnect()
  }, [])
}
