import { FastifyReply, FastifyRequest } from 'fastify'
import { AgendaService } from '@services/agenda-services'
import { PrismaAgenda } from '@repositories/prisma/prisma-agenda'

/**
 * Busca todas as agendas
 */
export async function getAllAgenda(
  request: FastifyRequest,
  reply: FastifyReply,
) {
  try {
    const agendaRepository = new PrismaAgenda()
    const agendaService = new AgendaService(agendaRepository)

    const { agendas, totalOnList } = await agendaService.findAll()

    // Busca as sessões de cada agenda
    const agendasWithSessions = await Promise.all(
      agendas.map(async (agenda) => {
        const sessions = await agendaRepository.getSessionsByAgendaId(agenda.id)
        const latestSession = sessions.length > 0 ? sessions[0] : null

        return {
          ...agenda,
          startDate: latestSession?.startTime || null,
          endDate: latestSession?.endTime || null,
        }
      }),
    )

    reply.status(200).send({
      message: '[OK]',
      agendas: agendasWithSessions,
      totalOnList,
    })
  } catch (error) {
    console.error('[getAllAgenda] [ERROR]', error)
    reply.status(500).send({ message: '[ERROR]', error })
  }
}

/**
 * Busca todas as agendas encerradas
 */
export async function getAllFinishedAgenda(
  request: FastifyRequest,
  reply: FastifyReply,
) {
  try {
    const agendaRepository = new PrismaAgenda()
    const agendaService = new AgendaService(agendaRepository)

    const { agendas, totalOnList } = await agendaService.findAllFinished()

    const agendasWithSessions = await Promise.all(
      agendas.map(async (agenda) => {
        const sessions = await agendaRepository.getSessionsByAgendaId(agenda.id)
        const latestSession = sessions.length > 0 ? sessions[0] : null

        return {
          ...agenda,
          startDate: latestSession?.startTime || null,
          endDate: latestSession?.endTime || null,
        }
      }),
    )

    reply.status(200).send({
      message: '[OK]',
      agendas: agendasWithSessions,
      totalOnList,
    })
  } catch (error) {
    console.error('[getAllFinishedAgenda] [ERROR]', error)
    reply.status(500).send({ message: '[ERROR]', error })
  }
}

/**
 * Busca todas as agendas abertas
 */
export async function getAllOpenAgenda(
  request: FastifyRequest,
  reply: FastifyReply,
) {
  try {
    const agendaRepository = new PrismaAgenda()
    const agendaService = new AgendaService(agendaRepository)

    const { agendas, totalOnList } = await agendaService.findAllOpen()

    const agendasWithSessions = await Promise.all(
      agendas.map(async (agenda) => {
        const sessions = await agendaRepository.getSessionsByAgendaId(agenda.id)
        const latestSession = sessions.length > 0 ? sessions[0] : null

        return {
          ...agenda,
          startDate: latestSession?.startTime || null,
          endDate: latestSession?.endTime || null,
        }
      }),
    )

    reply.status(200).send({
      message: '[OK]',
      agendas: agendasWithSessions,
      totalOnList,
    })
  } catch (error) {
    console.error('[getAllOpenAgenda] [ERROR]', error)
    reply.status(500).send({ message: '[ERROR]', error })
  }
}
