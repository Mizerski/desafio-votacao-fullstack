import { User } from '@/shared/types/user'

/**
 * Constantes para as chaves de storage
 */
export const STORAGE_KEYS = {
  ACCESS_TOKEN: '@votacao:accessToken',
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
   */
  saveTokens: (accessToken: string) => {
    localStorage.setItem(STORAGE_KEYS.ACCESS_TOKEN, accessToken)
  },

  /**
   * Recupera os tokens do localStorage
   * @returns Objeto com os tokens ou null se não existirem
   */
  getTokens: () => {
    const accessToken = localStorage.getItem(STORAGE_KEYS.ACCESS_TOKEN)

    if (!accessToken) return null

    return {
      accessToken,
    }
  },

  /**
   * Remove os tokens do localStorage
   */
  removeTokens: () => {
    localStorage.removeItem(STORAGE_KEYS.ACCESS_TOKEN)
  },

  /**
   * Limpa todos os dados de autenticação do localStorage
   */
  clear: () => {
    localStorage.removeItem(STORAGE_KEYS.USER)
    localStorage.removeItem(STORAGE_KEYS.ACCESS_TOKEN)
  },
}
