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
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { RadioGroup, RadioGroupItem } from '@/components/ui/radio-group'
import { Loader2, AlertCircle, Tag } from 'lucide-react'
import { Agenda, AgendaVote } from '@/shared/types/agenda'
import { toast, Toaster } from 'sonner'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import { EmptyVoting } from './empty-voting'
import { useAgenda } from '@/shared/hooks/use-agenda'
import { storage } from '@/lib/storage'
import { useVote } from '@/shared/hooks/use-vote'
import { useTabsContext } from '../../contexts/tabs-context'
import { useSelectedAgenda } from '../../contexts/selected-agenda-context'

export function AgendaVoting() {
  const [loading, setLoading] = useState<boolean>(true)
  const [error, setError] = useState<string | null>(null)
  const [selectedAgenda, setSelectedAgenda] = useState<Agenda | null>(null)
  const [userId, setUserId] = useState<string>('')
  const [voteOption, setVoteOption] = useState<AgendaVote>()
  const [isSubmitting, setIsSubmitting] = useState<boolean>(false)

  const { getAllOpenAgenda, openAgendas } = useAgenda()
  const { createVote } = useVote()
  const { setActiveTab } = useTabsContext()
  const { selectedAgenda: contextSelectedAgenda } = useSelectedAgenda()

  useEffect(() => {
    if (contextSelectedAgenda) {
      const agendaInOpenList = openAgendas.find(
        (a) => a.id === contextSelectedAgenda.id,
      )
      if (agendaInOpenList) {
        setSelectedAgenda(agendaInOpenList)
      }
    }
  }, [contextSelectedAgenda, openAgendas])

  useEffect(() => {
    if (openAgendas.length === 1 && !selectedAgenda) {
      setSelectedAgenda(openAgendas[0])
    }
  }, [openAgendas, selectedAgenda])

  useEffect(() => {
    const loadingTimeout = setTimeout(() => {
      setLoading(true)
      getAllOpenAgenda()
      setLoading(false)
    }, 1000)

    return () => clearTimeout(loadingTimeout)
  }, [])

  useEffect(() => {
    const user = storage.getUser()
    if (user) setUserId(user.id)
  }, [])

  async function handleSubmitVote() {
    if (!selectedAgenda || !voteOption || !userId) {
      toast.error('Preencha todos os campos para votar.')
      return
    }

    try {
      setIsSubmitting(true)

      await createVote({
        userId,
        agendaId: selectedAgenda.id,
        vote: voteOption,
      })

      toast.success('Voto registrado com sucesso!')

      setVoteOption(undefined)
    } catch (error) {
      console.error('Erro ao registrar voto:', error)
      toast.error('Erro ao registrar voto!')
    } finally {
      setIsSubmitting(false)
    }
  }

  if (loading) {
    return (
      <div className="flex justify-center items-center h-64">
        <Loader2 className="h-8 w-8 animate-spin text-primary" />
        <span className="ml-2">Carregando sessões ativas...</span>
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

  if (openAgendas.length === 0) {
    return <EmptyVoting />
  }

  return (
    <Card className="max-w-2xl mx-auto">
      <CardHeader>
        <CardTitle>Votação</CardTitle>
        <CardDescription>
          Participe da votação em uma pauta ativa
        </CardDescription>
      </CardHeader>

      <CardContent className="space-y-6">
        {openAgendas.length > 1 && (
          <div className="space-y-2">
            <Label htmlFor="agenda-select">Selecione a Pauta</Label>
            <Select
              value={selectedAgenda?.id || ''}
              onValueChange={(value) => {
                const selected = openAgendas.find(
                  (agenda) => agenda.id === value,
                )
                if (selected) setSelectedAgenda(selected)
              }}
            >
              <SelectTrigger className="w-full">
                <SelectValue placeholder="Selecione uma pauta" />
              </SelectTrigger>
              <SelectContent>
                {openAgendas.map((agenda) => (
                  <SelectItem key={agenda.id} value={agenda.id}>
                    {agenda.title}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>
        )}

        {selectedAgenda && (
          <>
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

            <div className="space-y-4">
              <div className="space-y-2">
                <Label htmlFor="userId">ID do Usuário</Label>
                <Input
                  id="userId"
                  placeholder="Digite seu ID de usuário"
                  value={userId}
                  disabled
                />
              </div>

              <div className="space-y-2">
                <Label>Seu Voto</Label>
                <RadioGroup
                  value={voteOption || ''}
                  onValueChange={(value) => setVoteOption(value as AgendaVote)}
                >
                  <div className="flex items-center space-x-2">
                    <RadioGroupItem value={AgendaVote.YES} id="vote-yes" />
                    <Label htmlFor="vote-yes">Sim</Label>
                  </div>
                  <div className="flex items-center space-x-2">
                    <RadioGroupItem value={AgendaVote.NO} id="vote-no" />
                    <Label htmlFor="vote-no">Não</Label>
                  </div>
                </RadioGroup>
              </div>
            </div>
          </>
        )}
      </CardContent>
      <CardFooter className="flex flex-col gap-4">
        <div className="flex gap-4 w-full">
          <Button
            variant="outline"
            className="flex-1"
            onClick={() => setActiveTab('agendas')}
          >
            Voltar
          </Button>
          <Button
            variant="default"
            className="flex-1"
            disabled={isSubmitting || !voteOption || !userId}
            onClick={handleSubmitVote}
          >
            {isSubmitting ? (
              <>
                <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                Enviando...
              </>
            ) : (
              'Confirmar Voto'
            )}
          </Button>
        </div>
      </CardFooter>
      <Toaster />
    </Card>
  )
}
