import { useState, useMemo } from 'react'
import { Agenda, AgendaCategory, AgendaStatus } from '../../../shared/types/agenda'

interface UseAgendaFiltersProps {
  agendas: Agenda[]
}

interface UseAgendaFiltersReturn {
  filteredAgendas: Agenda[]
  statusFilter: AgendaStatus
  categoryFilter: AgendaCategory
  searchTerm: string
  setStatusFilter: (status: AgendaStatus) => void
  setCategoryFilter: (category: AgendaCategory) => void
  setSearchTerm: (term: string) => void
  clearFilters: () => void
}

/**
 * Hook para gerenciar a lógica de filtragem das agendas
 * @param props - Propriedades do hook
 * @returns Funções e estados para filtragem
 */
export function useAgendaFilters({ agendas }: UseAgendaFiltersProps): UseAgendaFiltersReturn {
  const [statusFilter, setStatusFilter] = useState<AgendaStatus>(AgendaStatus.ALL)
  const [categoryFilter, setCategoryFilter] = useState<AgendaCategory>(AgendaCategory.ALL)
  const [searchTerm, setSearchTerm] = useState<string>('')

  const filteredAgendas = useMemo(() => {
    return agendas.filter((agenda) => {
      const matchesStatus = statusFilter === AgendaStatus.ALL || agenda.status === statusFilter
      const matchesCategory = categoryFilter === AgendaCategory.ALL || agenda.category === categoryFilter
      const matchesSearch = searchTerm === '' || 
        agenda.title.toLowerCase().includes(searchTerm.toLowerCase()) ||
        agenda.description.toLowerCase().includes(searchTerm.toLowerCase())

      return matchesStatus && matchesCategory && matchesSearch
    })
  }, [agendas, statusFilter, categoryFilter, searchTerm])

  const clearFilters = () => {
    setStatusFilter(AgendaStatus.ALL)
    setCategoryFilter(AgendaCategory.ALL)
    setSearchTerm('')
  }

  return {
    filteredAgendas,
    statusFilter,
    categoryFilter,
    searchTerm,
    setStatusFilter,
    setCategoryFilter,
    setSearchTerm,
    clearFilters,
  }
} 