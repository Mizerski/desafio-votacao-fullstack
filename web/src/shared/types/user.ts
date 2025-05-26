export interface User {
  id: string
  name: string
  email: string
  role: string
  isActive: boolean
  isEmailVerified: boolean
  lastLogin: string | null
}

export interface AuthResponse {
  token: string
  tokenType: string
  expiresIn: number
  user: User
} 