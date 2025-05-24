import { AgendaRepo } from '@repositories/agenda-repo'
import { NotExistsError } from '@shared/errors/not-exists'
import { Agenda, Prisma } from '@prisma/client'

export class AgendaService {
  constructor(private readonly agendaRepo: AgendaRepo) {}

  /**
   * Cria uma nova pauta
   * @param agenda Dados da pauta a ser criada
   * @returns Pauta criada
   */
  async create(agenda: Prisma.AgendaCreateInput): Promise<Agenda> {
    return this.agendaRepo.create(agenda)
  }

  /**
   * Busca todas as pautas
   * @returns Todas as pautas
   */
  async findAll() {
    const agendas = await this.agendaRepo.findAll()

    const totalOnList = await this.agendaRepo.count()

    return { agendas, totalOnList }
  }

  /**
   * Busca todas as pautas encerradas
   * @returns Todas as pautas encerradas
   */
  async findAllFinished() {
    const agendas = await this.agendaRepo.findAllFinished()

    const totalOnList = await this.agendaRepo.count()

    return { agendas, totalOnList }
  }

  /**
   * Busca todas as pautas abertas
   * @returns Todas as pautas abertas
   */
  async findAllOpen() {
    const agendas = await this.agendaRepo.findAllOpen()

    const totalOnList = await this.agendaRepo.count()

    return { agendas, totalOnList }
  }

  /**
   * Busca uma pauta pelo ID
   * @param id ID da pauta
   * @returns Pauta encontrada
   */
  async findById(id: string) {
    const agenda = await this.agendaRepo.findById(id)
    if (!agenda) {
      throw new NotExistsError('Pauta')
    }
    return { agenda }
  }

  /**
   * Atualiza uma pauta existente
   * @param id ID da pauta a ser atualizada
   * @param agenda Dados da pauta a ser atualizada
   * @returns Pauta atualizada
   */
  async update(id: string, agenda: Agenda): Promise<Agenda> {
    const existingAgenda = await this.agendaRepo.findById(id)
    if (!existingAgenda) {
      throw new NotExistsError('Pauta')
    }
    return this.agendaRepo.update(id, agenda)
  }

  /**
   * Deleta uma pauta existente
   * @param id ID da pauta a ser deletada
   */
  async delete(id: string): Promise<void> {
    const existingAgenda = await this.agendaRepo.findById(id)
    if (!existingAgenda) {
      throw new NotExistsError('Pauta')
    }
    return this.agendaRepo.delete(id)
  }
}
