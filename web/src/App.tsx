import { Routes, Route } from 'react-router'
import { LoginPage } from '@/pages/login'
import { HomePage } from './pages/home'

function App() {
  return (
    <Routes>
      <Route path="/" element={<LoginPage />} />
      <Route path="/home" element={<HomePage />} />
    </Routes>
  )
}

export default App
