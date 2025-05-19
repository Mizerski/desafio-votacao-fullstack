import { FastifyInstance } from 'fastify'
import { createAgenda } from 'src/http/create-agenda'
import { getAllAgenda } from 'src/http/get-agenda'

export async function agendaRoutes(server: FastifyInstance) {
  server.post('/agenda', createAgenda)
  server.get('/agenda', getAllAgenda)
}
