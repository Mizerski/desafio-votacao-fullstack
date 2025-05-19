import { VotesService } from '@services/votes-services'
import { FastifyReply, FastifyRequest } from 'fastify'
import { createVoteBodySchema } from './schemas'
import { PrismaVotesRepo } from '@repositories/prisma/prisma-votes'

export async function createVote(request: FastifyRequest, reply: FastifyReply) {
  const { userId, agendaId, vote } = createVoteBodySchema.parse(request.body)

  try {
    const votesRepository = new PrismaVotesRepo()
    const votesService = new VotesService(votesRepository)

    const voteCreated = await votesService.create({
      user: { connect: { id: userId } },
      agenda: { connect: { id: agendaId } },
      vote,
    })

    reply.status(201).send({
      message: '[CREATED]',
      vote: voteCreated,
    })
  } catch (error) {
    console.error('[createVote] [ERROR]', error)
    if (error instanceof Error) {
      reply.status(400).send({
        message: error.message,
      })
    }

    reply.status(500).send({
      message: '[INTERNAL SERVER ERROR]',
    })

    throw error
  }
}
