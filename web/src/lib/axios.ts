import axios from 'axios'
import { storage } from './storage'

const api = axios.create({
  baseURL: 'http://localhost:8181',
})

/**
 * Interceptor para adicionar o token de acesso em todas as requisições
 */
api.interceptors.request.use((config) => {
  const tokens = storage.getTokens()
  if (tokens?.accessToken) {
    config.headers.Authorization = `Bearer ${tokens.accessToken}`
  }
  return config
})

/**
 * Interceptor para tratar erros de autenticação e refresh token
 */
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config

    // Se o erro for 401 (não autorizado) e não for uma tentativa de refresh
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true

      try {
        const tokens = storage.getTokens()

        if (!tokens?.refreshToken) {
          throw new Error('Refresh token não encontrado')
        }

        const response = await api.post('/refresh-token', {
          token: tokens.refreshToken,
        })

        const { accessToken, refreshToken } = response.data

        storage.saveTokens(accessToken, refreshToken)

        // Atualiza o token no header da requisição original
        originalRequest.headers.Authorization = `Bearer ${accessToken}`

        // Repete a requisição original com o novo token
        return api(originalRequest)
      } catch (refreshError) {
        // Se falhar o refresh, limpa os tokens e redireciona para o login
        storage.clear()
        window.location.href = '/'
        return Promise.reject(refreshError)
      }
    }

    return Promise.reject(error)
  },
)

export { api }
