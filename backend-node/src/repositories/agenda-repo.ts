import { Agenda, Prisma, Session } from '@prisma/client'

export interface AgendaRepo {
  /**
   * Cria uma nova agenda
   * @param agenda Agenda a ser criada
   * @returns Agenda criada
   */
  create(agenda: Prisma.AgendaCreateInput): Promise<Agenda>

  /**
   * Busca todas as agendas
   * @returns Todas as agendas
   */
  findAll(): Promise<Agenda[]>

  /**
   * Busca todas as agendas encerradas
   * @returns Todas as agendas encerradas
   */
  findAllFinished(): Promise<Agenda[]>

  /**
   * Busca todas as agendas abertas
   * @returns Todas as agendas abertas
   */
  findAllOpen(): Promise<Agenda[]>

  /**
   * Busca uma agenda pelo ID
   * @param id ID da agenda
   * @returns Agenda encontrada ou null se não encontrada
   */
  findById(id: string): Promise<Agenda | null>

  /**
   * Atualiza uma agenda existente
   * @param id ID da agenda a ser atualizada
   * @param agenda Agenda com os dados atualizados
   */
  update(id: string, agenda: Partial<Agenda>): Promise<Agenda>

  /**
   * Deleta uma agenda existente
   * @param id ID da agenda a ser deletada
   */
  delete(id: string): Promise<void>

  /**
   * Conta o número de agendas
   * @param where Condições de contagem
   * @returns Número de agendas
   */
  count(where?: Prisma.AgendaWhereInput): Promise<number>

  /**
   * Cria uma nova sessão para uma agenda
   * @param session Dados da sessão
   */
  createSession(session: {
    agendaId: string
    startTime: Date
    endTime: Date
  }): Promise<void>

  /**
   * Busca todas as sessões de uma agenda
   * @param agendaId ID da agenda
   * @returns Lista de sessões
   */
  getSessionsByAgendaId(agendaId: string): Promise<Session[]>
}
