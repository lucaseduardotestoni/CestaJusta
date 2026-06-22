import { useLayoutEffect } from 'react'

/*
 * Fase 2 do redesign: reveal em cascata + parallax leve no hero.
 *
 * - Reveal: adiciona `.reveal` (escondido via CSS) aos blocos da landing e
 *   dispara `.in` quando cada um entra na viewport (uma vez). Itens de grupos
 *   (passos, cards, stats) entram em cascata via transition-delay incremental.
 * - Parallax: a cesta do hero desloca devagar conforme o scroll.
 *
 * Respeita prefers-reduced-motion: não esconde nada e não aplica parallax.
 * As classes `.reveal` são adicionadas por JS (não no HTML), então sem o hook
 * o conteúdo aparece normalmente.
 */

const SOLO =
  '.hero-text, .hero-visual, .sec-head, .sobre-grid > div:first-child, ' +
  '.den-stats, .seal, .comm-narr, .cta-box'

const GROUPS = [
  ['.timeline', '.step'],
  ['.stat-stack', '.stat-card'],
  ['.prod-grid', '.prod'],
  ['.den-cards', '.den'],
  ['.comm-stats', '.cstat'],
]

export default function useReveal() {
  useLayoutEffect(() => {
    const root = document.querySelector('.landing')
    if (!root) return
    const reduce = window.matchMedia('(prefers-reduced-motion: reduce)').matches
    if (reduce) return // CSS mantém tudo visível; sem reveal/parallax

    const cleanups = []

    // ---- Reveal em cascata ----
    const targets = []
    root.querySelectorAll(SOLO).forEach((el) => {
      el.classList.add('reveal')
      targets.push(el)
    })
    GROUPS.forEach(([groupSel, itemSel]) => {
      root.querySelectorAll(groupSel).forEach((group) => {
        group.querySelectorAll(itemSel).forEach((el, i) => {
          el.classList.add('reveal')
          el.style.transitionDelay = i * 80 + 'ms'
          targets.push(el)
        })
      })
    })

    if ('IntersectionObserver' in window) {
      const io = new IntersectionObserver(
        (entries) => {
          entries.forEach((en) => {
            if (en.isIntersecting) {
              en.target.classList.add('in')
              io.unobserve(en.target)
            }
          })
        },
        { threshold: 0.15, rootMargin: '0px 0px -8% 0px' },
      )
      targets.forEach((el) => io.observe(el))
      cleanups.push(() => io.disconnect())
    } else {
      targets.forEach((el) => el.classList.add('in'))
    }

    // ---- Parallax leve no hero ----
    const basket = root.querySelector('.basket-img')
    if (basket) {
      let raf = 0
      const onScroll = () => {
        if (raf) return
        raf = requestAnimationFrame(() => {
          raf = 0
          const y = window.scrollY || window.pageYOffset || 0
          // --py é o offset do parallax; a animação hero-bob soma o float por cima
          basket.style.setProperty('--py', y * 0.08 + 'px')
        })
      }
      window.addEventListener('scroll', onScroll, { passive: true })
      onScroll()
      cleanups.push(() => {
        window.removeEventListener('scroll', onScroll)
        if (raf) cancelAnimationFrame(raf)
      })
    }

    return () => cleanups.forEach((fn) => fn())
  }, [])
}
