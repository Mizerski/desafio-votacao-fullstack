import { FastifyReply, FastifyRequest } from 'fastify'
import { z } from 'zod'
import { AgendaService } from '@services/agenda-services'
import { PrismaAgenda } from '@repositories/prisma/prisma-agenda'
import { AgendaTimerService } from '@services/agenda-timer-service'
import { startSessionBodySchema } from './schemas'

/**
 * Controlador para iniciar uma sessão de votação
 */
export async function startAgendaSession(
  request: FastifyRequest,
  reply: FastifyReply,
) {
  try {
    const { agendaId, durationInMinutes } = startSessionBodySchema.parse(
      request.body,
    )

    const agendaRepo = new PrismaAgenda()
    const agendaService = new AgendaService(agendaRepo)
    const agendaTimerService = new AgendaTimerService(agendaRepo)

    const { agenda } = await agendaService.findById(agendaId)

    await agendaTimerService.startTimer(agendaId, durationInMinutes)

    const sessions = await agendaRepo.getSessionsByAgendaId(agendaId)
    const latestSession =
      sessions.length > 0 ? sessions[sessions.length - 1] : null

    reply.status(200).send({
      message: '[SESSION_STARTED]',
      agenda: {
        ...agenda,
        startDate: latestSession?.startTime,
        endDate: latestSession?.endTime,
      },
    })
  } catch (error) {
    console.error('[startAgendaSession] [ERROR]', error)

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
