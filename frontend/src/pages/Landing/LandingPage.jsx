import './Landing.css'
import LandingNav from './components/LandingNav'
import Hero from './components/Hero'
import ComoFunciona from './components/ComoFunciona'
import Sobre from './components/Sobre'
import Produtos from './components/Produtos'
import Denuncias from './components/Denuncias'
import Comunidade from './components/Comunidade'
import CtaFinal from './components/CtaFinal'
import LandingFooter from './components/LandingFooter'

export default function LandingPage() {
  return (
    <div className="landing" id="top">
      <LandingNav />
      <Hero />
      <ComoFunciona />
      <Sobre />
      <Produtos />
      <Denuncias />
      <Comunidade />
      <CtaFinal />
      <LandingFooter />
    </div>
  )
}
