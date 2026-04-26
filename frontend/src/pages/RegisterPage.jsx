import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { cadastrar } from '../services/api'
import './HomePage.css'

export default function RegisterPage() {
  const [showPassword, setShowPassword] = useState(false)
  const [name, setName] = useState('')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [tipoUsuario, setTipoUsuario] = useState('CONSUMIDOR')
  const [acceptTerms, setAcceptTerms] = useState(false)
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const navigate = useNavigate()

  async function handleSubmit(e) {
    e.preventDefault()
    setError('')

    if (!acceptTerms) {
      setError('Você deve aceitar os Termos e Condições.')
      return
    }

    setLoading(true)
    try {
      await cadastrar(name, email, password, tipoUsuario)
      navigate('/')
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="page">
      <nav className="navbar">
        <div className="nav-links">
          <a href="/" className="nav-link">Início</a>
          <a href="#" className="nav-link">Sobre nós</a>
          <a href="#" className="nav-link">Produtos</a>
          <a href="#" className="nav-link">Denúncias</a>
        </div>
      </nav>

      <main className="hero">
        <div className="hero-left">
          <h1 className="hero-title">Denuncie preços altos</h1>
          <p className="hero-subtitle">
            Verifique os melhores locais<br />
            para comprar produtos da<br />
            cesta básica
          </p>
          <button className="btn-denuncie">Denuncie</button>
        </div>

        <div className="hero-right">
          <div className="green-circle" />

          <form className="login-card" onSubmit={handleSubmit}>
            <h2 className="card-title">Registre-se</h2>
            <p className="card-subtitle">
              Já possui uma conta?{' '}
              <a href="/" className="link-orange">Entre aqui</a>
            </p>

            <div className="form-group">
              <label className="form-label">Nome:</label>
              <input
                type="text"
                className="form-input"
                placeholder="Digite seu nome"
                value={name}
                onChange={e => setName(e.target.value)}
                required
              />
            </div>

            <div className="form-group">
              <label className="form-label">Endereço de E-mail:</label>
              <input
                type="email"
                className="form-input"
                placeholder="seuemail@exemplo.com"
                value={email}
                onChange={e => setEmail(e.target.value)}
                required
              />
            </div>

            <div className="form-group">
              <label className="form-label">Senha:</label>
              <div className="input-password-wrapper">
                <input
                  type={showPassword ? 'text' : 'password'}
                  className="form-input"
                  placeholder="Crie uma senha"
                  value={password}
                  minLength={6}
                  onChange={e => setPassword(e.target.value)}
                  required
                />
                <button
                  type="button"
                  className="btn-eye"
                  onClick={() => setShowPassword(v => !v)}
                  aria-label="Mostrar senha"
                >
                  {showPassword ? (
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#888" strokeWidth="2">
                      <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94"/>
                      <path d="M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19"/>
                      <line x1="1" y1="1" x2="23" y2="23"/>
                    </svg>
                  ) : (
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#888" strokeWidth="2">
                      <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/>
                      <circle cx="12" cy="12" r="3"/>
                    </svg>
                  )}
                </button>
              </div>
              <p className="form-hint">Escolha uma senha com, no mínimo, 6 caracteres.</p>
            </div>

            <div className="form-group">
              <label className="form-label">Tipo de conta:</label>
              <select
                className="form-input"
                value={tipoUsuario}
                onChange={e => setTipoUsuario(e.target.value)}
              >
                <option value="CONSUMIDOR">Consumidor</option>
                <option value="COMERCIANTE">Comerciante</option>
              </select>
            </div>

            <div className="form-group form-check">
              <input
                type="checkbox"
                id="terms"
                checked={acceptTerms}
                onChange={e => setAcceptTerms(e.target.checked)}
              />
              <label htmlFor="terms" className="form-check-label">
                Eu aceito os{' '}
                <a href="#" className="link-orange">Termos e Condições</a>
                {' '}e li e entendi a{' '}
                <a href="#" className="link-orange">Política de Privacidade</a>
              </label>
            </div>

            {error && <p className="form-error">{error}</p>}

            <button type="submit" className="btn-proximo" disabled={loading}>
              {loading ? 'Cadastrando...' : 'Criar conta'}
            </button>
          </form>
        </div>
      </main>
    </div>
  )
}
