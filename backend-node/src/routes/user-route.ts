import { FastifyInstance } from 'fastify'
import { createUser } from '../http/create-user'

export async function userRoutes(server: FastifyInstance) {
  server.post('/users', createUser)
}
