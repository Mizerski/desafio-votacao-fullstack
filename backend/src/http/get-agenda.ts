import { FastifyReply, FastifyRequest } from 'fastify'
import { AgendaService } from '@services/agenda-services'
import { PrismaAgenda } from '@repositories/prisma/prisma-agenda'

export async function getAllAgenda(
  request: FastifyRequest,
  reply: FastifyReply,
) {
  try {
    const agendaRepository = new PrismaAgenda()
    const agendaService = new AgendaService(agendaRepository)

    const { agendas, totalOnList } = await agendaService.findAll()

    reply.status(200).send({
      message: '[OK]',
      agendas,
      totalOnList,
    })
  } catch (error) {
    console.error('[createAgenda] [ERROR]', error)
  }
}

export async function getAllFinishedAgenda(
  request: FastifyRequest,
  reply: FastifyReply,
) {
  try {
    const agendaRepository = new PrismaAgenda()
    const agendaService = new AgendaService(agendaRepository)

    const { agendas, totalOnList } = await agendaService.findAllFinished()

    reply.status(200).send({
      message: '[OK]',
      agendas,
      totalOnList,
    })
  } catch (error) {
    console.error('[getAllFinishedAgenda] [ERROR]', error)
  }
}

export async function getAllOpenAgenda(
  request: FastifyRequest,
  reply: FastifyReply,
) {
  try {
    const agendaRepository = new PrismaAgenda()
    const agendaService = new AgendaService(agendaRepository)

    const { agendas, totalOnList } = await agendaService.findAllOpen()

    reply.status(200).send({
      message: '[OK]',
      agendas,
      totalOnList,
    })
  } catch (error) {
    console.error('[getAllOpenAgenda] [ERROR]', error)
  }
}
