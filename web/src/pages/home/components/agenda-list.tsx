import { useEffect, useState } from 'react'
import { Agenda } from '../types/agenda'
import { AgendaCard } from './agenda-card'
import { AgendaFilters } from './agenda-filters'
import {
  AgendaLoadingState,
  AgendaErrorState,
  AgendaEmptyState,
} from './agenda-states'
import { useAgendaFilters } from '../hooks/use-agenda-filters'
import { useAgendaLoading } from '../hooks/use-agenda-loading'

/**
 * Componente principal que exibe a lista de agendas
 * @returns Lista de agendas com filtros e estados
 */
export function AgendaList() {
  const { loading, error, setLoading, retry } = useAgendaLoading()
  const [agendas, setAgendas] = useState<Agenda[]>([])

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
      setAgendas(mockAgendas)
      setLoading(false)
    }, 1000)

    return () => clearTimeout(loadingTimeout)
  }, [setLoading])

  const mockAgendas: Agenda[] = [
    {
      id: '1',
      title: 'Aprovação do orçamento anual',
      description:
        'Votação para aprovação do orçamento anual da cooperativa para o próximo exercício financeiro.',
      status: 'em_andamento',
      category: 'Projetos',
      session: {
        endTime: '2025-05-19T10:00:00Z',
        startTime: '2025-05-19T09:00:00Z',
      },
    },
    {
      id: '2',
      title: 'Avaliação do diretor executivo',
      description:
        'Avaliação do diretor executivo da cooperativa para o exercício financeiro atual.',
      status: 'encerrada',
      category: 'Eleições',
      session: {
        endTime: '2025-05-19T10:00:00Z',
        startTime: '2025-05-19T09:00:00Z',
      },
    },
    {
      id: '3',
      title: 'Reunião anual da cooperativa',
      description:
        'Reunião anual da cooperativa para discussão e aprovação de assuntos relevantes.',
      status: 'não_iniciada',
      category: 'Estatutário',
      session: undefined,
    },
    {
      id: '4',
      title: 'Reunião de diretores',
      description:
        'Reunião de diretores da cooperativa para discussão e aprovação de assuntos relevantes.',
      status: 'cancelada',
      category: 'Administrativo',
      session: undefined,
    },
  ]

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
