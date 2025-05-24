import { Votes, Prisma } from '@prisma/client'

export interface VotesRepo {
  /**
   * Cria um novo voto
   * @param votes Dados do voto
   * @returns Voto criado
   */
  create(votes: Prisma.VotesCreateInput): Promise<Votes>

  /**
   * Busca todos os votos
   * @returns Todos os votos
   */
  findAll(): Promise<Votes[]>

  /**
   * Conta o número de votos
   * @returns Número de votos
   */
  count(where?: Prisma.VotesWhereInput): Promise<number>

  /**
   * Busca um voto pelo ID do usuário e da pauta
   * @param userId ID do usuário
   * @param agendaId ID da pauta
   * @returns Voto encontrado
   */
  findByUserIdAndAgendaId(
    userId: string,
    agendaId: string,
  ): Promise<Votes | null>
}
