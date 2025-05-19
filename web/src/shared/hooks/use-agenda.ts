import { api } from '@/lib/axios'
import { Agenda, AgendaVote } from '@/shared/types/agenda'
import { useState } from 'react'

type CreateAgendaInput = Omit<Agenda, 'id' | 'votes'>

export function useAgenda() {
  const [agendas, setAgendas] = useState<Agenda[]>([])
  const [totalOnList, setTotalOnList] = useState<number>(0)
  const [finishedAgendas, setFinishedAgendas] = useState<Agenda[]>([])
  const [openAgendas, setOpenAgendas] = useState<Agenda[]>([])

  async function getAllAgenda() {
    try {
      const response = await api.get<{
        agendas: Agenda[]
        totalOnList: number
      }>('/agenda')

      const { agendas, totalOnList } = response.data

      setAgendas(agendas)
      setTotalOnList(totalOnList)

      return { agendas, totalOnList }
    } catch (error) {
      console.error('Erro ao buscar agendas:', error)
      return { agendas: [], totalOnList: 0 }
    }
  }

  async function getAllFinishedAgenda() {
    try {
      const response = await api.get<{
        agendas: Agenda[]
        totalOnList: number
      }>('/agenda/finished')

      const { agendas, totalOnList } = response.data

      setFinishedAgendas(agendas)
      setTotalOnList(totalOnList)

      return { agendas, totalOnList }
    } catch (error) {
      console.error('Erro ao buscar agendas encerradas:', error)
      return { agendas: [], totalOnList: 0 }
    }
  }

  async function getAllOpenAgenda() {
    try {
      const response = await api.get<{
        agendas: Agenda[]
        totalOnList: number
      }>('/agenda/open')

      const { agendas, totalOnList } = response.data
      setOpenAgendas(agendas)
      setTotalOnList(totalOnList)
    } catch (error) {
      console.error('Erro ao buscar agendas abertas:', error)
    }
  }

  async function createAgenda(agenda: CreateAgendaInput) {
    try {
      const { data } = await api.post('/agenda', agenda)

      setAgendas([...agendas, data])

      return data
    } catch (error) {
      console.error('Erro ao criar agenda:', error)
      throw error
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
    try {
      const { data } = await api.post('/agenda/start-session', {
        agendaId,
        durationInMinutes,
      })

      // Atualiza as agendas no estado
      setAgendas((prev) =>
        prev.map((agenda) => (agenda.id === agendaId ? data.agenda : agenda)),
      )

      return data.agenda
    } catch (error) {
      console.error('Erro ao iniciar sessão de votação:', error)
      throw error
    }
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
    try {
      const { data } = await api.post('/votes', {
        agendaId,
        userId,
        vote,
      })

      // Atualiza as agendas no estado
      setAgendas((prev) =>
        prev.map((agenda) => (agenda.id === agendaId ? data.agenda : agenda)),
      )

      return data.agenda
    } catch (error) {
      console.error('Erro ao votar na pauta:', error)
      throw error
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
