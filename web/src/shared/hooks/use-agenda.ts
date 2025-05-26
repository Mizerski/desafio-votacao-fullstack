import { api } from '@/lib/axios'
import { Agenda, AgendaVote } from '@/shared/types/agenda'
import { ApiError, ApiResponse } from '@wmmz/fn-api-client'
import { useState } from 'react'

type CreateAgendaInput = Omit<Agenda, 'id' | 'votes'>

export function useAgenda() {
  const [agendas, setAgendas] = useState<Agenda[]>([])
  const [totalOnList, setTotalOnList] = useState<number>(0)
  const [finishedAgendas, setFinishedAgendas] = useState<Agenda[]>([])
  const [openAgendas, setOpenAgendas] = useState<Agenda[]>([])

  /**
   * Busca todas as agendas
   * @returns Dados das agendas
   */
  async function getAllAgenda() {
    await api.get<{
      agendas: Agenda[]
      totalOnList: number
    }>('/agenda', {
      onSuccess: (
        response: ApiResponse<{
          agendas: Agenda[]
          totalOnList: number
        }>,
      ) => {
        const { agendas, totalOnList } = response.data
        setAgendas(agendas)
        setTotalOnList(totalOnList)
      },
      onError: (error: ApiError) => {
        console.error('Erro ao buscar agendas:', error)
      },
    })
  }

  /**
   * Busca todas as agendas encerradas
   * @returns Dados das agendas encerradas
   */
  async function getAllFinishedAgenda() {
    await api.get<{
      agendas: Agenda[]
      totalOnList: number
    }>('/agenda/finished', {
      onSuccess: (
        response: ApiResponse<{
          agendas: Agenda[]
          totalOnList: number
        }>,
      ) => {
        const { agendas, totalOnList } = response.data
        setFinishedAgendas(agendas)
        setTotalOnList(totalOnList)
      },
      onError: (error: ApiError) => {
        console.error('Erro ao buscar agendas encerradas:', error)
        return { agendas: [], totalOnList: 0 }
      },
    })
  }

  /**
   * Busca todas as agendas abertas
   * @param page - Página atual
   * @param limit - Limite de agendas por página
   * @returns Dados das agendas abertas
   */
  async function getAllOpenAgenda() {
    await api.get<{
      agendas: Agenda[]
      totalOnList: number
    }>('/agenda/open', {
      onSuccess: (
        response: ApiResponse<{
          agendas: Agenda[]
          totalOnList: number
        }>,
      ) => {
        const { agendas, totalOnList } = response.data
        setOpenAgendas(agendas)
        setTotalOnList(totalOnList)
      },
      onError: (error: ApiError) => {
        console.error('Erro ao buscar agendas abertas:', error)
        return { agendas: [], totalOnList: 0 }
      },
    })
  }

  /**
   * Cria uma nova pauta
   * @param agenda Dados da pauta a ser criada
   * @returns Dados da pauta criada
   */
  async function createAgenda(agenda: CreateAgendaInput) {
    await api.post('/agenda', agenda, {
      onSuccess: (
        response: ApiResponse<{
          agenda: Agenda
        }>,
      ) => {
        setAgendas([...agendas, response.data.agenda])
      },
      onError: (error: ApiError) => {
        console.error('Erro ao criar agenda:', error)
        return { agenda: null }
      },
    })
  }

  /**
   * Inicia uma sessão de votação
   * @param agendaId ID da pauta
   * @param durationInMinutes Duração em minutos
   * @returns Dados da agenda atualizada
   */
  async function startAgendaSession(
    agendaId: string,
    durationInMinutes: number,
  ) {
    await api.post(
      '/agenda/start-session',
      {
        agendaId,
        durationInMinutes,
      },
      {
        onSuccess: (
          response: ApiResponse<{
            agenda: Agenda
          }>,
        ) => {
          setAgendas((prev) =>
            prev.map((agenda) =>
              agenda.id === agendaId ? response.data.agenda : agenda,
            ),
          )
        },
        onError: (error: ApiError) => {
          console.error('Erro ao iniciar sessão de votação:', error)
        },
      },
    )
  }

  /**
   * Registra um voto em uma pauta
   * @param agendaId ID da pauta
   * @param userId ID do usuário
   * @param vote Tipo de voto (YES/NO)
   * @returns Dados da agenda atualizada
   */
  async function voteOnAgenda(
    agendaId: string,
    userId: string,
    vote: AgendaVote,
  ) {
    await api.post(
      '/votes',
      {
        agendaId,
        userId,
        vote,
      },
      {
        onSuccess: (
          response: ApiResponse<{
            agenda: Agenda
          }>,
        ) => {
          setAgendas((prev) =>
            prev.map((agenda) =>
              agenda.id === agendaId ? response.data.agenda : agenda,
            ),
          )
        },
        onError: (error: ApiError) => {
          console.error('Erro ao votar na pauta:', error)
        },
      },
    )
  }

  return {
    getAllAgenda,
    createAgenda,
    agendas,
    totalOnList,
    getAllFinishedAgenda,
    finishedAgendas,
    getAllOpenAgenda,
    openAgendas,
    startAgendaSession,
    voteOnAgenda,
  }
}
