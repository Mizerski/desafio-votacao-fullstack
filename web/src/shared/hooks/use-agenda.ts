import { api } from '@/lib/api-client'
import { AGENDA, VOTE } from '@/lib/endpoints'
import { Agenda, AgendaVote } from '@/shared/types/agenda'
import { ApiError, ApiResponse } from '@wmmz/fn-api-client'
import { useState } from 'react'

type CreateAgendaInput = Pick<Agenda, 'title' | 'description' | 'category'>

interface PagedResponse<T> {
  content: T[]
  page: number
  size: number
  totalElements: number
  totalPages: number
}

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
    await api.get<PagedResponse<Agenda>>(
      AGENDA.GET_ALL, 
      {
        onSuccess: (
          response: ApiResponse<PagedResponse<Agenda>>,
        ) => {
          const { content, totalElements } = response.data
          setAgendas(content)
          setTotalOnList(totalElements)
        },
        onError: (error: ApiError) => {
          console.error('Erro ao buscar agendas:', error)
        },
    },
   )

   return {
    agendas,
    totalOnList,
   }
  }

  /**
   * Busca todas as agendas encerradas
   * @returns Dados das agendas encerradas
   */
  async function getAllFinishedAgenda() {
    return new Promise<{ agendas: Agenda[]; totalOnList: number }>((resolve, reject) => {
      api.get<PagedResponse<Agenda>>(AGENDA.GET_ALL_FINISHED, {
        onSuccess: (response: ApiResponse<PagedResponse<Agenda>>) => {
          const { content, totalElements } = response.data
          setFinishedAgendas(content)
          setTotalOnList(totalElements)
          resolve({ agendas: content, totalOnList: totalElements })
        },
        onError: (error: ApiError) => {
          console.error('Erro ao buscar agendas encerradas:', error)
          reject(error)
        },
      })
    })
  }

  /**
   * Busca todas as agendas abertas
   * @returns Dados das agendas abertas
   */
  async function getAllOpenAgenda() {
    return new Promise<{ agendas: Agenda[]; totalOnList: number }>((resolve, reject) => {
      api.get<PagedResponse<Agenda>>(AGENDA.GET_ALL_OPEN, {
        onSuccess: (response: ApiResponse<PagedResponse<Agenda>>) => {
          const { content, totalElements } = response.data
          setOpenAgendas(content)
          setTotalOnList(totalElements)
          resolve({ agendas: content, totalOnList: totalElements })
        },
        onError: (error: ApiError) => {
          console.error('Erro ao buscar agendas abertas:', error)
          reject(error)
        },
      })
    })
  }

  /**
   * Cria uma nova pauta
   * @param agenda Dados da pauta a ser criada
   * @returns Dados da pauta criada
   */
  async function createAgenda(agenda: CreateAgendaInput) {
      await api.post<Agenda>(
        AGENDA.CREATE,
        agenda,
        {
          onSuccess: (response: ApiResponse<Agenda>) => {
            const newAgenda = response.data
            setAgendas([...agendas, newAgenda])
          },
          onError: (error: ApiError) => {
            console.error('Erro ao criar agenda:', error)
          },
        },
      )

      return {
        agenda,
      }
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
    return new Promise<{ agenda: Agenda }>((resolve, reject) => {
      api.post(
        AGENDA.START_SESSION.replace(':agendaId', agendaId),
        { durationInMinutes},
        {
          onSuccess: (response: ApiResponse<Agenda>) => {
            const updatedAgenda = response.data
            setAgendas((prev) =>
              prev.map((agenda) =>
                agenda.id === agendaId ? updatedAgenda : agenda,
              ),
            )
            resolve({ agenda: updatedAgenda })
          },
          onError: (error: ApiError) => {
            console.error('Erro ao iniciar sessão de votação:', error)
            reject(error)
          },
        },
      )
    })
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
    return new Promise<{ agenda: Agenda }>((resolve, reject) => {
      api.post(
        VOTE.CREATE,
        {
          agendaId,
          userId,
          vote,
        },
        {
          onSuccess: (response: ApiResponse<Agenda>) => {
            const updatedAgenda = response.data
            setAgendas((prev) =>
              prev.map((agenda) =>
                agenda.id === agendaId ? updatedAgenda : agenda,
              ),
            )
            resolve({ agenda: updatedAgenda })
          },
          onError: (error: ApiError) => {
            console.error('Erro ao votar na pauta:', error)
            reject(error)
          },
        },
      )
    })
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
