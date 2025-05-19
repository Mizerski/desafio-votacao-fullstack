import { FastifyInstance } from 'fastify'
import { createAgenda } from 'src/http/create-agenda'
import {
  getAllAgenda,
  getAllFinishedAgenda,
  getAllOpenAgenda,
} from 'src/http/get-agenda'

export async function agendaRoutes(server: FastifyInstance) {
  server.post('/agenda', createAgenda)
  server.get('/agenda', getAllAgenda)
  server.get('/agenda/finished', getAllFinishedAgenda)
  server.get('/agenda/open', getAllOpenAgenda)
}
