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
import { Loader2, AlertCircle, Tag } from 'lucide-react'
import { EmptyResult } from './empty-result'
import { AgendaResult } from '@/shared/types/agenda'
import { useAgenda } from '@/shared/hooks/use-agenda'

export function AgendaResults() {
  const [selectedAgendaId, setSelectedAgendaId] = useState<string | null>(null)
  const [loading, setLoading] = useState<boolean>(true)
  const [error, setError] = useState<string | null>(null)
  const { finishedAgendas, getAllFinishedAgenda } = useAgenda()

  useEffect(() => {
    const loadingTimeout = setTimeout(() => {
      getAllFinishedAgenda()
      setLoading(false)
    }, 1000)

    return () => clearTimeout(loadingTimeout)
  }, [setLoading])

  if (loading) {
    return (
      <div className="flex justify-center items-center h-64">
        <Loader2 className="h-8 w-8 animate-spin text-primary" />
        <span className="ml-2">Carregando pautas encerradas...</span>
      </div>
    )
  }

  if (error) {
    return (
      <div className="flex justify-center items-center h-64 flex-col">
        <AlertCircle className="h-8 w-8 text-destructive mb-2" />
        <p className="text-destructive">{error}</p>
        <Button
          variant="outline"
          className="mt-4"
          onClick={() => {
            setLoading(true)
            setError(null)
          }}
        >
          Tentar novamente
        </Button>
      </div>
    )
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
          <label htmlFor="agenda-select" className="text-sm font-medium">
            Selecione a Pauta
          </label>
          <select
            id="agenda-select"
            className="w-full p-2 border rounded-md"
            value={selectedAgendaId || ''}
            onChange={(e) => setSelectedAgendaId(e.target.value)}
          >
            {finishedAgendas.map((agenda) => (
              <option key={agenda.id} value={agenda.id}>
                {agenda.title}
              </option>
            ))}
          </select>
        </div>

        {selectedAgenda && (
          <div className="space-y-2">
            <h3 className="text-lg font-medium">{selectedAgenda.title}</h3>
            <p className="text-sm text-muted-foreground">
              {selectedAgenda.description}
            </p>

            {/* Categoria */}
            <div className="flex items-center text-sm mt-2">
              <Tag className="h-4 w-4 mr-1 text-gray-400" />
              <span className="mr-2">Categoria:</span>
              {selectedAgenda.category}
            </div>
          </div>
        )}

        <Separator />

        {loading ? (
          <div className="flex justify-center items-center py-8">
            <Loader2 className="h-6 w-6 animate-spin text-primary" />
            <span className="ml-2">Carregando resultados...</span>
          </div>
        ) : selectedAgenda?.result ? (
          <div className="space-y-6">
            <div className="grid grid-cols-2 gap-4">
              <Card className="">
                <CardHeader className="pb-2">
                  <CardTitle className="text-green-700 text-lg">
                    Votos SIM
                  </CardTitle>
                </CardHeader>
                <CardContent>
                  <div className="text-4xl font-bold text-green-600">
                    {selectedAgenda.votes.yes}
                  </div>
                  <div className="text-sm text-green-700">
                    {selectedAgenda.votes.yes > 0
                      ? `${Math.round((selectedAgenda.votes.yes / (selectedAgenda.votes.yes + selectedAgenda.votes.no)) * 100)}%`
                      : '0%'}
                  </div>
                </CardContent>
              </Card>

              <Card className="">
                <CardHeader className="pb-2">
                  <CardTitle className="text-red-700 text-lg">
                    Votos NÃO
                  </CardTitle>
                </CardHeader>
                <CardContent>
                  <div className="text-4xl font-bold text-red-600">
                    {selectedAgenda.votes.no}
                  </div>
                  <div className="text-sm text-red-700">
                    {selectedAgenda.votes.no > 0
                      ? `${Math.round((selectedAgenda.votes.no / (selectedAgenda.votes.yes + selectedAgenda.votes.no)) * 100)}%`
                      : '0%'}
                  </div>
                </CardContent>
              </Card>
            </div>

            <div className="space-y-2">
              <h4 className="font-medium">Resultado Final</h4>
              <div className="p-4 rounded-md">
                <div className="text-lg font-medium">
                  {selectedAgenda.result === AgendaResult.APPROVED
                    ? 'Pauta APROVADA'
                    : selectedAgenda.result === AgendaResult.REJECTED
                      ? 'Pauta REJEITADA'
                      : 'EMPATE'}
                </div>
                <div className="text-sm text-muted-foreground mt-1">
                  Total de votos:{' '}
                  {selectedAgenda.votes.yes + selectedAgenda.votes.no}
                </div>
              </div>
            </div>

            <div className="relative pt-1">
              <div className="flex mb-2 items-center justify-between">
                <div>
                  <span className="text-xs font-semibold inline-block py-1 px-2 uppercase rounded-full bg-green-100 text-green-600 border-green-400">
                    Sim
                  </span>
                </div>
                <div>
                  <span className="text-xs font-semibold inline-block py-1 px-2 uppercase rounded-full bg-red-100 text-red-600 border-red-400">
                    Não
                  </span>
                </div>
              </div>
              <div className="overflow-hidden h-2 mb-4 text-xs flex rounded bg-gray-200">
                <div
                  style={{
                    width:
                      selectedAgenda.votes.yes + selectedAgenda.votes.no > 0
                        ? `${(selectedAgenda.votes.yes / (selectedAgenda.votes.yes + selectedAgenda.votes.no)) * 100}%`
                        : '0%',
                  }}
                  className="shadow-none flex flex-col text-center whitespace-nowrap text-white justify-center bg-green-500"
                ></div>
                <div
                  style={{
                    width:
                      selectedAgenda.votes.yes + selectedAgenda.votes.no > 0
                        ? `${(selectedAgenda.votes.no / (selectedAgenda.votes.yes + selectedAgenda.votes.no)) * 100}%`
                        : '0%',
                  }}
                  className="shadow-none flex flex-col text-center whitespace-nowrap text-white justify-center bg-red-500"
                ></div>
              </div>
            </div>
          </div>
        ) : (
          <div className="text-center py-8">
            <p className="text-muted-foreground">
              Nenhum resultado disponível para esta pauta.
            </p>
          </div>
        )}
      </CardContent>
      <CardFooter>
        <Button variant="default" className="w-full" onClick={() => {}}>
          Voltar para Pautas
        </Button>
      </CardFooter>
    </Card>
  )
}
