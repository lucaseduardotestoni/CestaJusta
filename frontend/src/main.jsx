import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './App.jsx'
import { AuthProvider } from './context/AuthContext.jsx'
import { ToastProvider } from './components/Toast/ToastContext.jsx'
import { AcoesPrecoSessaoProvider } from './context/AcoesPrecoSessaoContext.jsx'
import './styles/tokens.css'
import './styles/reset.css'
import './index.css'

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <AuthProvider>
      <ToastProvider>
        <AcoesPrecoSessaoProvider>
          <App />
        </AcoesPrecoSessaoProvider>
      </ToastProvider>
    </AuthProvider>
  </React.StrictMode>,
)
