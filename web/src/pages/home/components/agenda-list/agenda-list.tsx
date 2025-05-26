import { useEffect, useState, useCallback } from 'react'
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
import { toast } from 'sonner'
import { useTabsContext } from '../../contexts/tabs-context'
import { useSelectedAgenda } from '../../contexts/selected-agenda-context'
import { Agenda, AgendaCategory } from '@/shared/types/agenda'
import { AgendaStatus } from '@/shared/types/agenda-status'
import { Button } from '@/components/ui/button'

/**
 * Componente principal que exibe a lista de agendas
 * @returns Lista de agendas com filtros e estados
 */
export function AgendaList() {
  const { loading, error, setLoading, setError, retry } = useAgendaLoading()
  const { getAllAgenda, agendas, startAgendaSession } = useAgenda()
  const [processing, setProcessing] = useState<string | null>(null)
  const { setActiveTab } = useTabsContext()
  const { setSelectedAgenda } = useSelectedAgenda()

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
    async function loadAgendas() {
      try {
        const result = await getAllAgenda()

        if (result.agendas.length === 0) {
          console.log('Nenhuma agenda encontrada na API')
        }
      } catch (err) {
        console.error('Erro ao carregar agendas:', err)
        setError('Erro ao carregar pautas. Tente novamente.')
      } finally {
        setLoading(false)
      }
    }

    const loadingTimeout = setTimeout(() => {
      loadAgendas()
    }, 1000)

    return () => clearTimeout(loadingTimeout)
  }, [])

  // Atualiza as agendas a cada 30 segundos para sincronizar com o servidor
  useEffect(() => {
    const interval = setInterval(async () => {
      try {
        await getAllAgenda()
      } catch (err) {
        console.error('Erro ao atualizar agendas:', err)
      }
    }, 30000) // 30 segundos

    return () => clearInterval(interval)
  }, [])

  async function handleOpenSession(agendaId: string, duration: number) {
    try {
      setProcessing(agendaId)
      await startAgendaSession(agendaId, duration)
      toast.success(
        `Sessão iniciada por ${duration} ${duration === 1 ? 'minuto' : 'minutos'}`,
      )
      // Recarrega as agendas para atualizar os dados da sessão
      await getAllAgenda()
    } catch (error) {
      console.error('Erro ao iniciar sessão:', error)
      toast.error('Erro ao iniciar sessão de votação')
    } finally {
      setProcessing(null)
    }
  }

  function handleViewResults(agenda: Agenda) {
    setSelectedAgenda(agenda)
    setActiveTab('results')
  }

  function handleVote(agenda: Agenda) {
    setSelectedAgenda(agenda)
    setActiveTab('voting')
  }

  function handleCreateAgenda() {
    setActiveTab('create')
  }

  /**
   * Callback executado quando o timer de uma agenda termina
   * Recarrega as agendas para atualizar o status
   */
  const handleTimerEnd = useCallback(async () => {
    try {
      await getAllAgenda()
    } catch (error) {
      console.error('Erro ao atualizar agendas após timer:', error)
    }
  }, []) // Removida dependência para evitar loops infinitos

  if (loading) {
    return <AgendaLoadingState />
  }

  if (error) {
    return <AgendaErrorState error={error} onRetry={retry} />
  }

  if (agendas.length === 0) {
    return <AgendaEmptyState onCreateAgenda={handleCreateAgenda} />
  }

  if (filteredAgendas.length === 0) {
    if (
      statusFilter !== AgendaStatus.ALL ||
      categoryFilter !== AgendaCategory.ALL ||
      searchTerm !== ''
    ) {
      console.log('Filtros ativos, mas nenhuma agenda corresponde')
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
          <div className="text-center p-8">
            <h3 className="text-lg font-medium mb-2">
              Nenhuma pauta encontrada
            </h3>
            <p className="text-muted-foreground mb-4">
              Nenhuma pauta corresponde aos filtros selecionados
            </p>
            <Button variant="default" onClick={clearFilters}>
              Limpar Filtros
            </Button>
          </div>
        </div>
      )
    }

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
            isProcessing={processing === agenda.id}
            onOpenSession={(duration) => handleOpenSession(agenda.id, duration)}
            onViewResults={() => handleViewResults(agenda)}
            onVote={() => handleVote(agenda)}
            onTimerEnd={handleTimerEnd}
          />
        ))}
      </div>
    </div>
  )
}
