export interface User {
  id: string
  name: string
  email: string
  document: string | null
}

export interface AuthResponse {
  id: string
  name: string
  email: string
  document: string | null
  accessToken: string
  refreshToken: string
} 