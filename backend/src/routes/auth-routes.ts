import { FastifyInstance } from 'fastify'
import { authenticate, refreshTokens } from '../http/authenticate'

export async function authRoutes(server: FastifyInstance) {
  server.post('/authenticate', authenticate)
  server.post('/refresh-token', refreshTokens)
}
