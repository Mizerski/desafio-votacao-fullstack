import { AgendaRepo } from '@repositories/agenda-repo'
import { NotExistsError } from '@shared/errors/not-exists'
import { Agenda, Prisma } from '@prisma/client'

export class AgendaService {
  constructor(private readonly agendaRepo: AgendaRepo) {}

  async create(agenda: Prisma.AgendaCreateInput): Promise<Agenda> {
    return this.agendaRepo.create(agenda)
  }

  async findAll() {
    const agendas = await this.agendaRepo.findAll()

    const totalOnList = await this.agendaRepo.count()

    return { agendas, totalOnList }
  }

  async findAllFinished() {
    const agendas = await this.agendaRepo.findAllFinished()

    const totalOnList = await this.agendaRepo.count()

    return { agendas, totalOnList }
  }

  async findAllOpen() {
    const agendas = await this.agendaRepo.findAllOpen()

    const totalOnList = await this.agendaRepo.count()

    return { agendas, totalOnList }
  }

  async findById(id: string) {
    const agenda = await this.agendaRepo.findById(id)
    if (!agenda) {
      throw new NotExistsError('Pauta')
    }
    return { agenda }
  }

  async update(id: string, agenda: Agenda): Promise<Agenda> {
    const existingAgenda = await this.agendaRepo.findById(id)
    if (!existingAgenda) {
      throw new NotExistsError('Pauta')
    }
    return this.agendaRepo.update(id, agenda)
  }

  async delete(id: string): Promise<void> {
    const existingAgenda = await this.agendaRepo.findById(id)
    if (!existingAgenda) {
      throw new NotExistsError('Pauta')
    }
    return this.agendaRepo.delete(id)
  }
}
