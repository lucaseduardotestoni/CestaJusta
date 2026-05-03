import './Tabs.css'

export default function Tabs({ items, value, onChange }) {
  return (
    <div className="tabs" role="tablist">
      {items.map(item => (
        <button
          key={item.value}
          role="tab"
          aria-selected={item.value === value}
          className={`tab ${item.value === value ? 'tab-ativa' : ''}`}
          onClick={() => onChange(item.value)}
        >
          {item.label}
        </button>
      ))}
    </div>
  )
}
