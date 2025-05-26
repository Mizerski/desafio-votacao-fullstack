import { api } from '@/lib/api-client'
import { AGENDA, VOTE } from '@/lib/endpoints'
import { Agenda, AgendaVote } from '@/shared/types/agenda'
import { useState, useCallback } from 'react'

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
  const getAllAgenda = useCallback(async () => {
    try {
      const { data } = await api.get<PagedResponse<Agenda>>(AGENDA.GET_ALL)
      const { content, totalElements } = data
      setAgendas(content)
      setTotalOnList(totalElements)
      return { agendas: content, totalOnList: totalElements }
    } catch (error) {
      console.error('Erro ao buscar agendas:', error)
      return { agendas: [], totalOnList: 0 }
    }
  }, [])

  /**
   * Busca todas as agendas encerradas
   * @returns Dados das agendas encerradas
   */
  async function getAllFinishedAgenda() {
    try {
      const { data } = await api.get<PagedResponse<Agenda>>(AGENDA.GET_ALL_FINISHED)
      const { content, totalElements } = data
      setFinishedAgendas(content)
      setTotalOnList(totalElements)
      return { agendas: content, totalOnList: totalElements }
    } catch (error) {
      console.error('Erro ao buscar agendas encerradas:', error)
      return { agendas: [], totalOnList: 0 }
    }
  }

  /**
   * Busca todas as agendas abertas
   * @returns Dados das agendas abertas
   */
  async function getAllOpenAgenda() {
    try {
      const { data } = await api.get<PagedResponse<Agenda>>(AGENDA.GET_ALL_OPEN)
      const { content, totalElements } = data
      setOpenAgendas(content)
      setTotalOnList(totalElements)
      return { agendas: content, totalOnList: totalElements }
    } catch (error) {
      console.error('Erro ao buscar agendas abertas:', error)
      return { agendas: [], totalOnList: 0 }
    }
  }

  /**
   * Cria uma nova pauta
   * @param agenda Dados da pauta a ser criada
   * @returns Dados da pauta criada
   */
  async function createAgenda(agenda: CreateAgendaInput) {
    try {
      const { data: newAgenda } = await api.post<Agenda>(AGENDA.CREATE, agenda)
      setAgendas((prev) => [...prev, newAgenda])
      return { agenda: newAgenda }
    } catch (error) {
      console.error('Erro ao criar agenda:', error)
      return { agenda: null }
    }
  }

  /**
   * Inicia uma sessão de votação
   * @param agendaId ID da pauta
   * @param durationInMinutes Duração em minutos
   * @returns Dados da agenda atualizada
   */
  const startAgendaSession = useCallback(async (
    agendaId: string,
    durationInMinutes: number,
  ) => {
    try {
      const { data: updatedAgenda } = await api.post(
        AGENDA.START_SESSION.replace(':agendaId', agendaId),
        { durationInMinutes },
      )
      setAgendas((prev) =>
        prev.map((agenda) =>
          agenda.id === agendaId ? updatedAgenda : agenda,
        ),
      )
      return { agenda: updatedAgenda }
    } catch (error) {
      console.error('Erro ao iniciar sessão de votação:', error)
      return { agenda: null }
    }
  }, [])

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
    try {
      const { data: updatedAgenda } = await api.post(
        VOTE.CREATE,
        {
          agendaId,
          userId,
          vote,
        },
      )
      setAgendas((prev) =>
        prev.map((agenda) =>
          agenda.id === agendaId ? updatedAgenda : agenda,
        ),
      )
      return { agenda: updatedAgenda }
    } catch (error) {
      console.error('Erro ao votar na pauta:', error)
      return { agenda: null }
    }
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
