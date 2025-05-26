import { api } from '@/lib/api-client'
import { AgendaVote } from '../types/agenda'
import { ApiError, ApiResponse } from '@wmmz/fn-api-client'
import { VOTE } from '@/lib/endpoints'

interface CreateVoteInput {
  userId: string
  agendaId: string
  voteType: AgendaVote
}

export function useVote() {
  /**
   * Cria um novo voto
   * @param vote - Dados do voto a ser criado
   * @returns Dados do voto criado
   */
  async function createVote(vote: CreateVoteInput) {
    await api.post(VOTE.CREATE, vote, {
      onSuccess: (response: ApiResponse<{ vote: AgendaVote }>) => {
        return response.data.vote
      },
      onError: (error: ApiError) => {
        console.error('Erro ao criar voto:', error)
      },
    })
  }

  return { createVote }
}
