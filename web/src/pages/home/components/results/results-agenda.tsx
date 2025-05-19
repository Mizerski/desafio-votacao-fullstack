'use client'

import { useState, useEffect } from 'react'
import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Separator } from '@/components/ui/separator'
import { Agenda } from '@/shared/types/agenda'
import { useAgenda } from '@/shared/hooks/use-agenda'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import { Label } from '@/components/ui/label'
import { EmptyResult } from './empty-result'
import { LoadingState, ErrorState } from './loading-error-states'
import { ResultStatusBadge } from './result-status-badge'
import { AgendaInfoSection } from './agenda-info-section'
import { VoteCountSection } from './vote-count-section'
import { VotesVisualization } from './votes-visualization'
import { useTabsContext } from '../../contexts/tabs-context'
import { useSelectedAgenda } from '../../contexts/selected-agenda-context'

/**
 * Componente principal que exibe os resultados de uma pauta de votação
 */
export function AgendaResults() {
  const [selectedAgendaId, setSelectedAgendaId] = useState<string | null>(null)
  const [loading, setLoading] = useState<boolean>(true)
  const [error, setError] = useState<string | null>(null)
  const { finishedAgendas, getAllFinishedAgenda } = useAgenda()
  const { setActiveTab } = useTabsContext()
  const { selectedAgenda: contextSelectedAgenda } = useSelectedAgenda()

  // Efeito para selecionar automaticamente a agenda vinda do contexto
  useEffect(() => {
    if (contextSelectedAgenda) {
      setSelectedAgendaId(contextSelectedAgenda.id)
    }
  }, [contextSelectedAgenda])

  useEffect(() => {
    const loadingTimeout = setTimeout(() => {
      getAllFinishedAgenda()
        .then(() => setLoading(false))
        .catch((err) => {
          console.error('Erro ao carregar pautas:', err)
          setError('Não foi possível carregar as pautas encerradas.')
          setLoading(false)
        })
    }, 1000)

    return () => clearTimeout(loadingTimeout)
  }, [])

  // Funções auxiliares para cálculo de porcentagens
  const getYesPercentage = (agenda: Agenda) => {
    if (!agenda || !agenda.totalVotes || agenda.totalVotes === 0) return 0
    return Math.round(((agenda.yesVotes || 0) / agenda.totalVotes) * 100)
  }

  const getNoPercentage = (agenda: Agenda) => {
    if (!agenda || !agenda.totalVotes || agenda.totalVotes === 0) return 0
    return Math.round(((agenda.noVotes || 0) / agenda.totalVotes) * 100)
  }

  // Função para tentar novamente em caso de erro
  const handleRetry = () => {
    setLoading(true)
    setError(null)
    getAllFinishedAgenda()
      .then(() => setLoading(false))
      .catch(() => {
        setError('Não foi possível carregar as pautas encerradas.')
        setLoading(false)
      })
  }

  if (loading) {
    return <LoadingState />
  }

  if (error) {
    return <ErrorState error={error} onRetry={handleRetry} />
  }

  if (finishedAgendas.length === 0) {
    return <EmptyResult />
  }

  const selectedAgenda = finishedAgendas.find(
    (agenda) => agenda.id === selectedAgendaId,
  )

  return (
    <Card className="max-w-2xl mx-auto">
      <CardHeader>
        <CardTitle>Resultados da Votação</CardTitle>
        <CardDescription>
          Visualize os resultados das votações encerradas
        </CardDescription>
      </CardHeader>
      <CardContent className="space-y-6">
        <div className="space-y-2">
          <Label className="text-sm font-medium">Selecione a Pauta</Label>
          <Select
            value={selectedAgendaId || ''}
            onValueChange={(value) => setSelectedAgendaId(value)}
          >
            <SelectTrigger className="w-full">
              <SelectValue placeholder="Selecione a Pauta" />
            </SelectTrigger>
            <SelectContent>
              {finishedAgendas.map((agenda) => (
                <SelectItem key={agenda.id} value={agenda.id}>
                  {agenda.title}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
        </div>

        {selectedAgenda && (
          <div className="space-y-4">
            <div className="space-y-2">
              <div className="flex justify-between items-start">
                <h3 className="text-lg font-medium">{selectedAgenda.title}</h3>
                <ResultStatusBadge agenda={selectedAgenda} />
              </div>
              <p className="text-sm text-muted-foreground">
                {selectedAgenda.description}
              </p>
            </div>

            {/* Detalhes da sessão */}
            <div className="grid grid-cols-2 gap-4 pt-2">
              <AgendaInfoSection agenda={selectedAgenda} />
              <VoteCountSection
                agenda={selectedAgenda}
                getYesPercentage={getYesPercentage}
                getNoPercentage={getNoPercentage}
              />
            </div>

            <Separator />

            {/* Visualização dos resultados */}
            <VotesVisualization
              agenda={selectedAgenda}
              getYesPercentage={getYesPercentage}
              getNoPercentage={getNoPercentage}
            />
          </div>
        )}
      </CardContent>
      <CardFooter>
        <Button
          variant="default"
          className="w-full"
          onClick={() => setActiveTab('agendas')}
        >
          Voltar para Pautas
        </Button>
      </CardFooter>
    </Card>
  )
}
