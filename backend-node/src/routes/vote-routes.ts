import { FastifyInstance } from 'fastify'
import { createVote } from 'src/http/create-vote'

export async function voteRoutes(server: FastifyInstance) {
  server.post('/votes', createVote)
}
