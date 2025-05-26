import { api } from '@/lib/api-client'
import { VOTE } from '@/lib/endpoints'
import { AgendaVote } from '../types/agenda'
import { useState, useEffect } from 'react'

interface UserVoteResponse {
  id: string
  agendaId: string
  userId: string
  voteType: AgendaVote
  createdAt: string
}

/**
 * Hook para verificar se o usuário já votou em uma agenda
 * @param agendaId ID da agenda
 * @param userId ID do usuário
 * @returns Informações sobre o voto do usuário
 */
export function useUserVote(agendaId?: string, userId?: string) {
  const [userVote, setUserVote] = useState<AgendaVote | null>(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    if (!agendaId || !userId) {
      setUserVote(null)
      return
    }

    async function checkUserVote() {
      setLoading(true)
      setError(null)
      try {
        const url = VOTE.GET_BY_USER_ID_AND_AGENDA_ID
          .replace(':userId', userId!)
          .replace(':agendaId', agendaId!)
        const { data } = await api.get<UserVoteResponse>(url)
        setUserVote(data.voteType)
      } catch (err: any) {
        if (err.response?.status === 404) {
          setUserVote(null)
        } else {
          setError(err.response?.data?.message || 'Erro ao verificar voto do usuário')
        }
      } finally {
        setLoading(false)
      }
    }

    checkUserVote()
  }, [agendaId, userId])

  /**
   * Verifica se o usuário já votou
   */
  const hasVoted = userVote !== null

  return {
    userVote,
    hasVoted,
    loading,
    error,
  }
} 