import { VotesRepo } from '@repositories/votes-repo'
import { Votes, Prisma, AgendaStatus } from '@prisma/client'
import { AgendaRepo } from '@repositories/agenda-repo'
import { PrismaAgenda } from '@repositories/prisma/prisma-agenda'

export class VotesService {
  private agendaRepo: AgendaRepo

  constructor(private votesRepo: VotesRepo) {
    this.agendaRepo = new PrismaAgenda()
  }

  async create(votes: Prisma.VotesCreateInput) {
    const userId = votes.user.connect?.id
    const agendaId = votes.agenda.connect?.id

    if (!userId || !agendaId) {
      throw new Error('ID do usuário e da pauta são obrigatórios')
    }

    const agenda = await this.agendaRepo.findById(agendaId)

    if (!agenda) {
      throw new Error('Pauta não encontrada')
    }

    if (agenda.status !== AgendaStatus.OPEN) {
      throw new Error('Pauta não está aberta para votos')
    }

    const createdVote = await this.votesRepo.findByUserIdAndAgendaId(
      userId,
      agendaId,
    )

    if (createdVote) {
      throw new Error('Usuário já votou nesta pauta')
    }

    const vote = await this.votesRepo.create(votes)
    return { vote }
  }

  async findAll(): Promise<Votes[]> {
    const votes = await this.votesRepo.findAll()

    return votes
  }
}
