import { FastifyInstance } from 'fastify'
import { userRoutes } from './user-route'
import { authRoutes } from './auth-routes'
import { agendaRoutes } from './agenda-routes'
import { voteRoutes } from './vote-routes'

export async function mainRoutes(server: FastifyInstance) {
  server.get('/', async (_request, reply) => {
    return reply.status(200).send({ message: 'Servidor rodando normalmente' })
  })

  await userRoutes(server)
  await authRoutes(server)
  await agendaRoutes(server)
  await voteRoutes(server)
}
