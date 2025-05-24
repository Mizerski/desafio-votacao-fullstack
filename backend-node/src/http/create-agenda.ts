import { FastifyReply, FastifyRequest } from 'fastify'
import { AgendaService } from '@services/agenda-services'
import { PrismaAgenda } from '@repositories/prisma/prisma-agenda'
import { agendaBodySchema } from './schemas'

export async function createAgenda(
  request: FastifyRequest,
  reply: FastifyReply,
) {
  const { title, description, category, status, result } =
    agendaBodySchema.parse(request.body)

  try {
    const agendaRepository = new PrismaAgenda()
    const agendaService = new AgendaService(agendaRepository)

    const agendaCreated = await agendaService.create({
      title,
      description,
      category,
      status,
      result,
    })

    reply.status(201).send({
      message: '[CREATED]',
      agenda: agendaCreated,
    })
  } catch (error) {
    console.error('[createAgenda] [ERROR]', error)
  }
}
