import './Landing.css'
import LandingNav from './components/LandingNav'
import Hero from './components/Hero'
import ComoFunciona from './components/ComoFunciona'
import Sobre from './components/Sobre'

export default function LandingPage() {
  return (
    <div className="landing" id="top">
      <LandingNav />
      <Hero />
      <ComoFunciona />
      <Sobre />
    </div>
  )
}
