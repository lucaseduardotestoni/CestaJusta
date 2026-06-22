const PRODUTOS = [
  { nome: 'Arroz 5kg',    tipo: 'tipo 1 · agulhinha', preco: 'R$ 24,90', delta: '↓ 8%',  dir: 'down' },
  { nome: 'Feijão 1kg',   tipo: 'carioca',             preco: 'R$ 7,49',  delta: '↑ 5%',  dir: 'up'   },
  { nome: 'Leite 1L',     tipo: 'integral',             preco: 'R$ 4,29',  delta: '↓ 12%', dir: 'down' },
  { nome: 'Óleo 900ml',   tipo: 'soja',                preco: 'R$ 6,90',  delta: '↓ 4%',  dir: 'down' },
  { nome: 'Café 500g',    tipo: 'torrado e moído',      preco: 'R$ 18,90', delta: '↑ 9%',  dir: 'up'   },
  { nome: 'Ovos 12un',    tipo: 'brancos',              preco: 'R$ 11,90', delta: '↓ 3%',  dir: 'down' },
  { nome: 'Pão francês',  tipo: 'por kg',               preco: 'R$ 13,50', delta: '↓ 6%',  dir: 'down' },
  { nome: 'Açúcar 1kg',   tipo: 'refinado',             preco: 'R$ 4,49',  delta: '↑ 2%',  dir: 'up'   },
]

const ICONS = [
  /* Arroz */
  <svg key="arroz" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round">
    <path d="M4 9h16l-1 10a2 2 0 0 1-2 1.8H7A2 2 0 0 1 5 19L4 9Z"/>
    <path d="M8 9V6a4 4 0 0 1 8 0v3"/>
  </svg>,
  /* Feijão */
  <svg key="feijao" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round">
    <circle cx="7" cy="9" r="2.5"/>
    <circle cx="15" cy="7" r="2.5"/>
    <circle cx="11" cy="15" r="2.5"/>
    <circle cx="17" cy="14" r="2"/>
  </svg>,
  /* Leite */
  <svg key="leite" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round">
    <path d="M8 3h8l-1 4 1 2v10a2 2 0 0 1-2 2h-4a2 2 0 0 1-2-2V9l1-2-1-4Z"/>
  </svg>,
  /* Óleo */
  <svg key="oleo" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round">
    <path d="M7 4h7l3 3v13a1 1 0 0 1-1 1H7a1 1 0 0 1-1-1V5a1 1 0 0 1 1-1Z"/>
    <path d="M9 9h6M9 13h6"/>
  </svg>,
  /* Café */
  <svg key="cafe" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round">
    <path d="M5 11h11a4 4 0 0 1 0 8H8a3 3 0 0 1-3-3v-5Z"/>
    <path d="M16 11h2a3 3 0 0 1 0 6h-2M9 4c0 1.5 1 1.5 1 3M12 4c0 1.5 1 1.5 1 3"/>
  </svg>,
  /* Ovos */
  <svg key="ovos" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round">
    <ellipse cx="12" cy="13" rx="6" ry="8"/>
  </svg>,
  /* Pão */
  <svg key="pao" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round">
    <path d="M4 14c0-4 4-7 8-7s8 3 8 7a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2Z"/>
    <path d="M9 11h6"/>
  </svg>,
  /* Açúcar */
  <svg key="acucar" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round">
    <path d="M5 8h14v3a7 7 0 0 1-14 0V8Z"/>
    <path d="M12 18v3M9 21h6"/>
  </svg>,
]

export default function Produtos() {
  return (
    <section id="produtos" className="sec">
      <div className="wrap">
        <div className="sec-head">
          <span className="eyebrow"><span className="dot"></span>Produtos monitorados</span>
          <h2>O que a comunidade acompanha agora</h2>
          <p>Os itens da cesta básica mais comparados nas últimas 24 horas.</p>
        </div>

        <div className="prod-grid">
          {PRODUTOS.map((p, i) => (
            <div key={p.nome} className="prod">
              <div className="prod-ic">{ICONS[i]}</div>
              <div className="prod-name">{p.nome}</div>
              <div className="prod-meta">{p.tipo}</div>
              <div className="prod-row">
                <span className="prod-price">{p.preco}</span>
                <span className={`prod-trend prod-trend--${p.dir}`}>{p.delta}</span>
              </div>
            </div>
          ))}
        </div>
      </div>
    </section>
  )
}
