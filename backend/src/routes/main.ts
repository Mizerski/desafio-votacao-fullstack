import { FastifyInstance } from 'fastify'

export async function mainRoutes(app: FastifyInstance) {
  app.get('/', async (_request, reply) => {
    return reply.status(200).send({ message: 'Servidor rodando normalmente' })
  })
}
