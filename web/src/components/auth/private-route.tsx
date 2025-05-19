import { Navigate, Outlet } from 'react-router-dom'
import { useAuth } from '@/shared/hooks/use-auth'

/**
 * Componente para proteger rotas que requerem autenticação
 * @returns {JSX.Element} Redireciona para o login se não estiver autenticado
 */
export function PrivateRoute() {
  const { user } = useAuth()

  if (!user) {
    return <Navigate to="/" replace />
  }

  return <Outlet />
}
