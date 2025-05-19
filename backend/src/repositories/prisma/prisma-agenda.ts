import { Agenda, Prisma } from '@prisma/client'
import { AgendaRepo } from '../agenda-repo'
import { prismaClient } from '@prisma/index'

export class PrismaAgenda implements AgendaRepo {
  async create(agenda: Prisma.AgendaCreateInput): Promise<Agenda> {
    return prismaClient.agenda.create({ data: agenda })
  }

  async findAll(): Promise<Agenda[]> {
    return prismaClient.agenda.findMany()
  }

  async findById(id: string): Promise<Agenda | null> {
    return prismaClient.agenda.findUnique({ where: { id } })
  }

  async update(id: string, agenda: Prisma.AgendaUpdateInput): Promise<Agenda> {
    return prismaClient.agenda.update({ where: { id }, data: agenda })
  }

  async delete(id: string): Promise<void> {
    await prismaClient.agenda.delete({ where: { id } })
  }

  async count(where?: Prisma.AgendaWhereInput): Promise<number> {
    return prismaClient.agenda.count({ where })
  }
}
