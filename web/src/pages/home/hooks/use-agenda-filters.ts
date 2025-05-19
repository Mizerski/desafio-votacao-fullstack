import { useState, useMemo, useEffect } from 'react'
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
  
  // Debug para ajudar a entender os dados
  useEffect(() => {
    console.log('Agendas disponíveis:', agendas.length)
    console.log('Status filter:', statusFilter)
    console.log('Category filter:', categoryFilter)
  }, [agendas, statusFilter, categoryFilter])

  const filteredAgendas = useMemo(() => {
    return agendas.filter((agenda) => {
      // Verificar status
      const matchesStatus = 
        statusFilter === AgendaStatus.ALL || 
        agenda.status === statusFilter
      
      // Verificar categoria
      const matchesCategory = 
        categoryFilter === AgendaCategory.ALL || 
        agenda.category === categoryFilter
      
      // Verificar termo de busca
      const matchesSearch = 
        searchTerm === '' || 
        agenda.title.toLowerCase().includes(searchTerm.toLowerCase()) ||
        agenda.description.toLowerCase().includes(searchTerm.toLowerCase())

      const result = matchesStatus && matchesCategory && matchesSearch
      return result
    })
  }, [agendas, statusFilter, categoryFilter, searchTerm])

  // Debug para ajudar a entender o resultado da filtragem
  useEffect(() => {
    console.log('Agendas filtradas:', filteredAgendas.length)
  }, [filteredAgendas])

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