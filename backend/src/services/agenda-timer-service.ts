import { AgendaResult, AgendaStatus } from '@prisma/client'
import { AgendaRepo } from '@repositories/agenda-repo'

/**
 * Serviço responsável por gerenciar o timer e cálculo dos votos de uma pauta
 */
export class AgendaTimerService {
  constructor(private readonly agendaRepo: AgendaRepo) {}

  /**
   * Inicia o timer para uma pauta
   * @param agendaId ID da pauta
   * @param durationInMinutes Duração em minutos
   */
  async startTimer(agendaId: string, durationInMinutes: number): Promise<void> {
    const startTime = new Date()
    const endTime = new Date(startTime.getTime() + durationInMinutes * 60000)

    await this.agendaRepo.update(agendaId, {
      status: AgendaStatus.IN_PROGRESS,
    })

    await this.agendaRepo.createSession({
      agendaId,
      startTime,
      endTime,
    })

    setTimeout(async () => {
      await this.calculateFinalResult(agendaId)
    }, durationInMinutes * 60000)
  }

  /**
   * Atualiza o resultado parcial dos votos
   * @param agendaId ID da pauta
   * @param voteType Tipo do voto (YES/NO)
   */
  async updateVoteCount(
    agendaId: string,
    voteType: 'YES' | 'NO',
  ): Promise<void> {
    const agenda = await this.agendaRepo.findById(agendaId)
    if (!agenda) throw new Error('Pauta não encontrada')

    const updateData = {
      totalVotes: agenda.totalVotes + 1,
      ...(voteType === 'YES'
        ? { yesVotes: agenda.yesVotes + 1 }
        : { noVotes: agenda.noVotes + 1 }),
    }

    await this.agendaRepo.update(agendaId, updateData)
  }

  /**
   * Calcula o resultado final da pauta
   * @param agendaId ID da pauta
   */
  private async calculateFinalResult(agendaId: string): Promise<void> {
    const agenda = await this.agendaRepo.findById(agendaId)
    if (!agenda) throw new Error('Pauta não encontrada')

    let result: AgendaResult
    if (agenda.totalVotes === 0) {
      result = AgendaResult.UNVOTED
    } else if (agenda.yesVotes > agenda.noVotes) {
      result = AgendaResult.APPROVED
    } else if (agenda.noVotes > agenda.yesVotes) {
      result = AgendaResult.REJECTED
    } else {
      result = AgendaResult.TIE
    }

    const updateData = {
      status: AgendaStatus.FINISHED,
      result,
      isActive: false,
    }

    await this.agendaRepo.update(agendaId, updateData)
  }
}
