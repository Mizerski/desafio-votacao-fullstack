import { api } from '@/lib/axios'
import { AgendaVote } from '../types/agenda'
import { ApiError, ApiResponse } from '@wmmz/fn-api-client'

interface CreateVoteInput {
  userId: string
  agendaId: string
  vote: AgendaVote
}

export function useVote() {
  /**
   * Cria um novo voto
   * @param vote - Dados do voto a ser criado
   * @returns Dados do voto criado
   */
  async function createVote(vote: CreateVoteInput) {
    await api.post('/votes', vote, {
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
