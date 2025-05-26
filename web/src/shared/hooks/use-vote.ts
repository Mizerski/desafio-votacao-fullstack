import { api } from '@/lib/api-client'
import { AgendaVote } from '../types/agenda'
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
    try {
      const { data } = await api.post(VOTE.CREATE, vote)
      return data
    } catch (error) {
      console.error('Erro ao criar voto:', error)
      return null
    }
  }

  return { createVote }
}
