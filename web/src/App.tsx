import { Routes, Route } from 'react-router'
import { LoginPage } from '@/pages/login'
import { HomePage } from './pages/home'
import { PrivateRoute } from '@/components/auth/private-route'

function App() {
  return (
    <Routes>
      <Route path="/" element={<LoginPage />} />
      <Route element={<PrivateRoute />}>
        <Route path="/home" element={<HomePage />} />
      </Route>
    </Routes>
  )
}

export default App
