import { VotesRepo } from '@repositories/votes-repo'
import { Votes, Prisma, AgendaStatus } from '@prisma/client'
import { AgendaRepo } from '@repositories/agenda-repo'
import { PrismaAgenda } from '@repositories/prisma/prisma-agenda'
import { AgendaTimerService } from './agenda-timer-service'

export class VotesService {
  private agendaRepo: AgendaRepo
  private agendaTimerService: AgendaTimerService

  constructor(private votesRepo: VotesRepo) {
    this.agendaRepo = new PrismaAgenda()
    this.agendaTimerService = new AgendaTimerService(this.agendaRepo)
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

    if (
      agenda.status !== AgendaStatus.OPEN &&
      agenda.status !== AgendaStatus.IN_PROGRESS
    ) {
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

    if (agenda.status === AgendaStatus.OPEN) {
      await this.agendaTimerService.startTimer(agendaId, 5)
    }

    await this.agendaTimerService.updateVoteCount(agendaId, vote.vote)

    return { vote }
  }

  async findAll(): Promise<Votes[]> {
    const votes = await this.votesRepo.findAll()
    return votes
  }
}
