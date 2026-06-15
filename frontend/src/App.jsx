import { BrowserRouter, Routes, Route } from 'react-router-dom'
import HomePage from './pages/HomePage'
import RegisterPage from './pages/RegisterPage'
import VisaoGeralPage from './pages/VisaoGeral/VisaoGeralPage'
import ProdutosPage from './pages/Produtos/ProdutosPage'
import AppShell from './components/AppShell/AppShell'

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/register" element={<RegisterPage />} />
        <Route element={<AppShell />}>
          <Route path="/dashboard" element={<VisaoGeralPage />} />
          <Route path="/produtos" element={<ProdutosPage />} />
        </Route>
      </Routes>
    </BrowserRouter>
  )
}

export default App
