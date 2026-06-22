import './Landing.css'
import LandingNav from './components/LandingNav'
import Hero from './components/Hero'

export default function LandingPage() {
  return (
    <div className="landing" id="top">
      <LandingNav />
      <Hero />
    </div>
  )
}
