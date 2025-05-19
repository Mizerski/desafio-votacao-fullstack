import { useEffect } from 'react'
import { AgendaCard } from './agenda-card'
import {
  AgendaLoadingState,
  AgendaErrorState,
  AgendaEmptyState,
} from '../agenda-states'
import { useAgenda } from '@/shared/hooks/use-agenda'
import { useAgendaLoading } from '../../hooks/use-agenda-loading'
import { useAgendaFilters } from '../../hooks/use-agenda-filters'
import { AgendaFilters } from './agenda-filters'

/**
 * Componente principal que exibe a lista de agendas
 * @returns Lista de agendas com filtros e estados
 */
export function AgendaList() {
  const { loading, error, setLoading, retry } = useAgendaLoading()
  const { getAllAgenda, agendas } = useAgenda()

  const {
    filteredAgendas,
    statusFilter,
    categoryFilter,
    searchTerm,
    setStatusFilter,
    setCategoryFilter,
    setSearchTerm,
    clearFilters,
  } = useAgendaFilters({ agendas })

  useEffect(() => {
    const loadingTimeout = setTimeout(() => {
      getAllAgenda()
      setLoading(false)
    }, 1000)

    return () => clearTimeout(loadingTimeout)
  }, [setLoading])

  function handleOpenSession(duration: number) {
    // Implementar lógica para abrir sessão
    console.log('Abrir sessão por', duration, 'minutos')
  }

  function handleViewResults() {
    // Implementar lógica para ver resultados
    console.log('Ver resultados')
  }

  function handleVote() {
    // Implementar lógica para votar
    console.log('Votar agora')
  }

  function handleCreateAgenda() {
    // Implementar lógica para criar agenda
    console.log('Criar nova agenda')
  }

  if (loading) {
    return <AgendaLoadingState />
  }

  if (error) {
    return <AgendaErrorState error={error} onRetry={retry} />
  }

  if (filteredAgendas.length === 0) {
    return <AgendaEmptyState onCreateAgenda={handleCreateAgenda} />
  }

  return (
    <div className="space-y-4">
      <AgendaFilters
        statusFilter={statusFilter}
        categoryFilter={categoryFilter}
        searchTerm={searchTerm}
        totalAgendas={filteredAgendas.length}
        onStatusChange={setStatusFilter}
        onCategoryChange={setCategoryFilter}
        onSearchChange={setSearchTerm}
        onClearFilters={clearFilters}
      />
      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
        {filteredAgendas.map((agenda) => (
          <AgendaCard
            key={agenda.id}
            agenda={agenda}
            onOpenSession={handleOpenSession}
            onViewResults={handleViewResults}
            onVote={handleVote}
          />
        ))}
      </div>
    </div>
  )
}
