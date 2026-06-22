import { BrowserRouter, Routes, Route } from 'react-router-dom'
import LandingPage from './pages/Landing/LandingPage'
import LoginPage from './pages/Login/LoginPage'
import RegisterPage from './pages/RegisterPage'
import VisaoGeralPage from './pages/VisaoGeral/VisaoGeralPage'
import ProdutosPage from './pages/Produtos/ProdutosPage'
import MercadosPage from './pages/Mercados/MercadosPage'
import DenunciasPage from './pages/Denuncias/DenunciasPage'
import UsuariosPage from './pages/Usuarios/UsuariosPage'
import NotFoundPage from './pages/NotFound/NotFoundPage'
import AppShell from './components/AppShell/AppShell'

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<LandingPage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
        <Route element={<AppShell />}>
          <Route path="/dashboard" element={<VisaoGeralPage />} />
          <Route path="/produtos" element={<ProdutosPage />} />
          <Route path="/mercados" element={<MercadosPage />} />
          <Route path="/denuncias" element={<DenunciasPage escopo="todas" />} />
          <Route path="/denuncias/minhas" element={<DenunciasPage escopo="minhas" />} />
          <Route path="/usuarios" element={<UsuariosPage />} />
          <Route path="*" element={<NotFoundPage />} />
        </Route>
      </Routes>
    </BrowserRouter>
  )
}

export default App
