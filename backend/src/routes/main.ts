import { FastifyInstance } from 'fastify'
import { userRoutes } from './user-route'
import { authRoutes } from './auth-routes'

export async function mainRoutes(server: FastifyInstance) {
  server.get('/', async (_request, reply) => {
    return reply.status(200).send({ message: 'Servidor rodando normalmente' })
  })

  await userRoutes(server)
  await authRoutes(server)
}
