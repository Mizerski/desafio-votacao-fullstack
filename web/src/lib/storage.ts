import { User } from '@/shared/types/user'

/**
 * Constantes para as chaves de storage
 */
export const STORAGE_KEYS = {
  ACCESS_TOKEN: '@votacao:accessToken',
  REFRESH_TOKEN: '@votacao:refreshToken',
  USER: '@votacao:user',
} as const

/**
 * Funções para gerenciar o storage do usuário
 */
export const storage = {
  /**
   * Salva os dados do usuário no localStorage
   * @param user - Dados do usuário
   */
  saveUser: (user: User) => {
    localStorage.setItem(STORAGE_KEYS.USER, JSON.stringify(user))
  },

  /**
   * Recupera os dados do usuário do localStorage
   * @returns Dados do usuário ou null se não existir
   */
  getUser: (): User | null => {
    const storedUser = localStorage.getItem(STORAGE_KEYS.USER)
    if (!storedUser) return null

    try {
      return JSON.parse(storedUser)
    } catch (error) {
      console.error('Erro ao carregar usuário do localStorage:', error)
      return null
    }
  },

  /**
   * Remove os dados do usuário do localStorage
   */
  removeUser: () => {
    localStorage.removeItem(STORAGE_KEYS.USER)
  },

  /**
   * Salva os tokens no localStorage
   * @param accessToken - Token de acesso
   * @param refreshToken - Token de refresh
   */
  saveTokens: (accessToken: string, refreshToken: string) => {
    localStorage.setItem(STORAGE_KEYS.ACCESS_TOKEN, accessToken)
    localStorage.setItem(STORAGE_KEYS.REFRESH_TOKEN, refreshToken)
  },

  /**
   * Recupera os tokens do localStorage
   * @returns Objeto com os tokens ou null se não existirem
   */
  getTokens: () => {
    const accessToken = localStorage.getItem(STORAGE_KEYS.ACCESS_TOKEN)
    const refreshToken = localStorage.getItem(STORAGE_KEYS.REFRESH_TOKEN)

    if (!accessToken || !refreshToken) return null

    return {
      accessToken,
      refreshToken,
    }
  },

  /**
   * Remove os tokens do localStorage
   */
  removeTokens: () => {
    localStorage.removeItem(STORAGE_KEYS.ACCESS_TOKEN)
    localStorage.removeItem(STORAGE_KEYS.REFRESH_TOKEN)
  },

  /**
   * Limpa todos os dados de autenticação do localStorage
   */
  clear: () => {
    localStorage.removeItem(STORAGE_KEYS.USER)
    localStorage.removeItem(STORAGE_KEYS.ACCESS_TOKEN)
    localStorage.removeItem(STORAGE_KEYS.REFRESH_TOKEN)
  },
}
