import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { api } from '@/lib/axios'
import { User, AuthResponse } from '@/types/user'
import { storage } from '@/lib/storage'

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
    try {
      setIsLoading(true)
      setError(null)

      const response = await api.post<AuthResponse>('/authenticate', {
        email,
        password,
      })

      const { accessToken, refreshToken, ...userData } = response.data

      storage.saveTokens(accessToken, refreshToken)
      storage.saveUser(userData)

      setUser(userData)

      navigate('/home', { replace: true })
    } catch (err) {
      setError('Email ou senha inválidos')
      console.error('Erro ao fazer login:', err)
    } finally {
      setIsLoading(false)
    }
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
