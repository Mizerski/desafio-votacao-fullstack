import { api } from '@/lib/axios'
import { AgendaVote } from '../types/agenda'

interface CreateVoteInput {
  userId: string
  agendaId: string
  vote: AgendaVote
}

export function useVote() {
  async function createVote(vote: CreateVoteInput) {
    try {
      const { data } = await api.post('/votes', vote)

      return data
    } catch (error) {
      console.error('Erro ao criar voto:', error)
      throw error
    }
  }

  return { createVote }
}
