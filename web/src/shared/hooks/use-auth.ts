import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { api } from '@/lib/api-client'
import { User, AuthResponse } from '@/shared/types/user'
import { storage } from '@/lib/storage'
import { ApiError, ApiResponse } from '@wmmz/fn-api-client'
import { AUTH } from '@/lib/endpoints'

/**
 * Hook personalizado para gerenciar a autenticação do usuário
 * @returns {Object} Objeto contendo funções e estados relacionados à autenticação
 */
export function useAuth() {
  const [isLoading, setIsLoading] = useState<boolean>(false)
  const [error, setError] = useState<string | null>(null)
  const [user, setUser] = useState<User | null>(() => storage.getUser())
  const navigate = useNavigate()

  useEffect(() => {
    const storedUser = storage.getUser()
    if (storedUser) {
      setUser(storedUser)
      if (window.location.pathname === '/') {
        navigate('/home')
      }
    }
  }, [navigate])

  /**
   * Função para realizar o login do usuário
   * @param {string} email - Email do usuário
   * @param {string} password - Senha do usuário
   */
  async function login(email: string, password: string) {
    setIsLoading(true)
    setError(null)

    await api.post<AuthResponse>(
      AUTH.LOGIN,
      {
        email,
        password,
      },
      {
        onSuccess: (response: ApiResponse<AuthResponse>) => {
          const { token, user: userData } = response.data
          storage.saveTokens(token, '')
          storage.saveUser(userData)
          setUser(userData)
          setIsLoading(false)
          navigate('/home', { replace: true })
        },
        onError: (error: ApiError) => {
          setError(error.message)
          console.error('Erro no login:', error)
          setIsLoading(false)
        },
      },
    )
  }

  /**
   * Função para realizar o logout do usuário
   */
  function logout() {
    storage.clear()
    setUser(null)
    navigate('/', { replace: true })
  }

  return {
    user,
    login,
    logout,
    isLoading,
    error,
  }
}
