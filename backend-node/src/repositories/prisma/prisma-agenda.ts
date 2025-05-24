import { Agenda, AgendaStatus, Prisma, Session } from '@prisma/client'
import { AgendaRepo } from '../agenda-repo'
import { prismaClient } from '@prisma/index'

export class PrismaAgenda implements AgendaRepo {
  async create(agenda: Prisma.AgendaCreateInput): Promise<Agenda> {
    return prismaClient.agenda.create({ data: agenda })
  }

  async findAll(): Promise<Agenda[]> {
    return prismaClient.agenda.findMany()
  }

  async findAllFinished(): Promise<Agenda[]> {
    return prismaClient.agenda.findMany({
      where: {
        status: { in: [AgendaStatus.FINISHED, AgendaStatus.CANCELLED] },
      },
    })
  }

  async findAllOpen(): Promise<Agenda[]> {
    return prismaClient.agenda.findMany({
      where: {
        status: { in: [AgendaStatus.OPEN, AgendaStatus.IN_PROGRESS] },
      },
    })
  }

  async findById(id: string): Promise<Agenda | null> {
    return prismaClient.agenda.findUnique({ where: { id } })
  }

  async update(id: string, agenda: Partial<Agenda>): Promise<Agenda> {
    return prismaClient.agenda.update({ where: { id }, data: agenda })
  }

  async delete(id: string): Promise<void> {
    await prismaClient.agenda.delete({ where: { id } })
  }

  async count(where?: Prisma.AgendaWhereInput): Promise<number> {
    return prismaClient.agenda.count({ where })
  }

  async createSession(session: {
    agendaId: string
    startTime: Date
    endTime: Date
  }): Promise<void> {
    await prismaClient.session.create({
      data: {
        agendaId: session.agendaId,
        startTime: session.startTime,
        endTime: session.endTime,
      },
    })
  }

  async getSessionsByAgendaId(agendaId: string): Promise<Session[]> {
    return prismaClient.session.findMany({
      where: { agendaId },
      orderBy: { startTime: 'desc' },
    })
  }
}
