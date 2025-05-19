import { FastifyReply, FastifyRequest } from 'fastify'
import { z } from 'zod'
import { VotesService } from '@services/votes-services'
import { PrismaVotesRepo } from '@repositories/prisma/prisma-votes'
import { PrismaAgenda } from '@repositories/prisma/prisma-agenda'
import { voteBodySchema } from './schemas'

/**
 * Controlador para registrar um voto
 */
export async function createVote(request: FastifyRequest, reply: FastifyReply) {
  try {
    const { agendaId, userId, vote } = voteBodySchema.parse(request.body)

    const votesRepo = new PrismaVotesRepo()
    const votesService = new VotesService(votesRepo)

    const { vote: createdVote } = await votesService.create({
      user: {
        connect: {
          id: userId,
        },
      },
      agenda: {
        connect: {
          id: agendaId,
        },
      },
      vote,
    })

    const agendaRepo = new PrismaAgenda()
    const agenda = await agendaRepo.findById(agendaId)

    const sessions = await agendaRepo.getSessionsByAgendaId(agendaId)
    const latestSession = sessions.length > 0 ? sessions[0] : null

    reply.status(201).send({
      message: '[CREATED]',
      vote: createdVote,
      agenda: {
        ...agenda,
        startDate: latestSession?.startTime || null,
        endDate: latestSession?.endTime || null,
      },
    })
  } catch (error) {
    console.error('[createVote] [ERROR]', error)

    if (error instanceof z.ZodError) {
      return reply.status(400).send({
        message: '[VALIDATION_ERROR]',
        errors: error.format(),
      })
    }

    return reply.status(500).send({
      message: '[ERROR]',
      error: (error as Error).message,
    })
  }
}
