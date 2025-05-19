'use client'

import { useEffect, useState } from 'react'
import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import { Loader2, Clock, AlertCircle } from 'lucide-react'
import { formatDistanceToNow } from "date-fns"
import { ptBR } from "date-fns/locale"
type AgendaStatus = 'em_andamento' | 'encerrada' | 'não_iniciada' | 'cancelada'

interface Agenda {
  id: string
  title: string
  description: string
  status: AgendaStatus
  session?: {
    endTime: string
    startTime: string
  }
}
export  function AgendaList() {
  const [loading, setLoading] = useState<boolean>(true)
  const [error, setError] = useState<string | null>(null)
  const [agendas, setAgendas] = useState<Agenda[]>([])

  useEffect(() => {
    const loadingTimeout = setTimeout(() => {
      setAgendas(mockAgendas)
      setLoading(false)
    }, 1000)

    return () => clearTimeout(loadingTimeout)
  }, [])

  const mockAgendas: Agenda[] = [
    {
      id: '1',
      title: 'Aprovação do orçamento anual',
      description: 'Votação para aprovação do orçamento anual da cooperativa para o próximo exercício financeiro.',
      status: 'em_andamento',
      session: {
        endTime: '2025-05-19T10:00:00Z',
        startTime: '2025-05-19T09:00:00Z',
      },
    },
    {
      id: '2',
      title: 'Avaliação do diretor executivo',
      description: 'Avaliação do diretor executivo da cooperativa para o exercício financeiro atual.',
      status: 'encerrada',
      session: {
        endTime: '2025-05-19T10:00:00Z',
        startTime: '2025-05-19T09:00:00Z',
      },
    },
    {
      id: '3',
      title: 'Reunião anual da cooperativa',
      description: 'Reunião anual da cooperativa para discussão e aprovação de assuntos relevantes.',
      status: 'não_iniciada',
      session: undefined,
    },
    {
      id: '4',
      title: 'Reunião de diretores',
      description: 'Reunião de diretores da cooperativa para discussão e aprovação de assuntos relevantes.',
      status: 'cancelada',
      session: undefined,
    },
  ]

  const getStatusBadge = (agenda: Agenda) => {
    if (agenda.status === 'não_iniciada') {
      return (
        <Badge variant="outline" >
          Não iniciada
        </Badge>
      )
    }

    if (agenda.status === 'encerrada') {
      return (
        <Badge className="bg-orange-100 text-orange-600 border-orange-400">
          Encerrada
        </Badge>
      )
    }

    if (agenda.status === 'em_andamento') {
      return (
        <Badge className="bg-green-100 text-green-600 border-green-400">
          Em andamento
        </Badge>
      )
    }

    return (
      <Badge variant="destructive">
        Cancelada
      </Badge>
    )
  }

  if (loading) {
    return (
      <div className="flex justify-center items-center h-64">
        <Loader2 className="h-8 w-8 animate-spin text-primary" />
        <span className="ml-2">Carregando pautas...</span>
      </div>
    )
  }

  if (error) {
    return (
      <div className="flex justify-center items-center h-64 flex-col">
        <AlertCircle className="h-8 w-8  mb-2" />
        <p className="">{error}</p>
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

  if (agendas.length === 0) {
    return (
      <div className="text-center p-8">
        <h3 className="text-lg font-medium mb-2">Nenhuma pauta cadastrada</h3>
        <p className="text-muted-foreground mb-4">
          Crie uma nova pauta para iniciar uma votação
        </p>
        <Button
          variant="default"
          onClick={() => {}}
        >
          Cadastrar Pauta
        </Button>
      </div>
    )
  }

  return (
    <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
      {agendas.map((agenda) => (
        <Card
          key={agenda.id}
          className="overflow-hidden"
        >
          <CardHeader className="pb-2">
            <div className="flex justify-between items-start">
              <CardTitle className="text-lg">{agenda.title}</CardTitle>
              {getStatusBadge(agenda)}
            </div>
            <CardDescription className="line-clamp-2">
              {agenda.description}
            </CardDescription>
          </CardHeader>
          <CardContent className="pb-2">
            {agenda.session ? (
              <div className="flex items-center text-sm text-muted-foreground">
                <Clock className="h-4 w-4 mr-1" />
                {agenda.session.endTime &&
                new Date(agenda.session.endTime) < new Date() ? (
                  <span>Encerrada há {formatDistanceToNow(new Date(agenda.session.endTime), { locale: ptBR, addSuffix: true })}</span>
                ) : (
                  <span>Encerra em {formatDistanceToNow(new Date(agenda.session.endTime), { locale: ptBR, addSuffix: true })}</span>
                )}
              </div>
            ) : (
              <div className="flex items-center text-sm text-muted-foreground">
                <Clock className="h-4 w-4 mr-1" />
                <span>Sessão não iniciada</span>
              </div>
            )}
          </CardContent>
          <CardFooter className="pt-2">
            {!agenda.session ? (
              <div className="flex gap-2 w-full">
                <Button
                  variant="default"
                  className="flex-1"
                  onClick={() => {}}
                >
                  Abrir Sessão (1 min)
                </Button>
                <Button
                  variant="outline"
                  className="flex-1"
                  onClick={() => {}}
                >
                  Abrir (5 min)
                </Button>
              </div>
            ) : agenda.session.endTime &&
              new Date(agenda.session.endTime) < new Date() ? (
              <Button
                variant="secondary"
                className="w-full"
                onClick={() => {}}
              >
                Ver Resultados
              </Button>
            ) : (
              <Button
                variant="default"
                className="w-full"
                onClick={() => {}}
              >
                Votar Agora
              </Button>
            )}
          </CardFooter>
        </Card>
      ))}
    </div>
  )
}
