import { api } from '@/lib/axios'
import { Agenda } from '@/shared/types/agenda'
import { useState } from 'react'

type CreateAgendaInput = Omit<Agenda, 'id' | 'votes'>

export function useAgenda() {
  const [agendas, setAgendas] = useState<Agenda[]>([])
  const [totalOnList, setTotalOnList] = useState<number>(0)

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

  return {
    getAllAgenda,
    createAgenda,
    agendas,
    totalOnList,
  }
}
