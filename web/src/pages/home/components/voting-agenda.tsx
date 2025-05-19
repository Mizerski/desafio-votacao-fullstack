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
import { Separator } from '@/components/ui/separator'
import { Loader2, Clock, AlertCircle, Tag } from 'lucide-react'
import { formatDistanceToNow } from 'date-fns'
import { ptBR } from 'date-fns/locale'
import { Agenda } from '../types/agenda'
import { toast, Toaster } from 'sonner'
import { mockAgendas } from './mock'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'

export function AgendaVoting() {
  const [loading, setLoading] = useState<boolean>(true)
  const [error, setError] = useState<string | null>(null)
  const [selectedAgenda, setSelectedAgenda] = useState<Agenda | null>(null)
  const [memberId, setMemberId] = useState<string>('')
  const [cpf, setCpf] = useState<string>('')
  const [voteOption, setVoteOption] = useState<'SIM' | 'NAO' | null>(null)
  const [isSubmitting, setIsSubmitting] = useState<boolean>(false)
  const [timeLeft, setTimeLeft] = useState<string | null>(null)
  const [activeAgendas, setActiveAgendas] = useState<Agenda[]>([])
  useEffect(() => {
    if (
      !selectedAgenda ||
      !selectedAgenda.session ||
      !selectedAgenda.session.endTime
    )
      return

    const updateTimeLeft = () => {
      if (
        !selectedAgenda ||
        !selectedAgenda.session ||
        !selectedAgenda.session.endTime
      )
        return

      const endTime = new Date(selectedAgenda.session.endTime)
      const now = new Date()

      if (now >= endTime) {
        setTimeLeft('Sessão encerrada')
        return
      }

      setTimeLeft(
        formatDistanceToNow(endTime, { addSuffix: false, locale: ptBR }),
      )
    }

    updateTimeLeft()
    const interval = setInterval(updateTimeLeft, 1000)
    return () => clearInterval(interval)
  }, [selectedAgenda])

  useEffect(() => {
    const loadingTimeout = setTimeout(() => {
      setActiveAgendas(mockAgendas)
      setLoading(false)
    }, 1000)

    return () => clearTimeout(loadingTimeout)
  }, [setLoading])

  const handleSubmitVote = async () => {
    if (!selectedAgenda || !voteOption || !memberId) {
      toast.error('Preencha todos os campos para votar.')
      return
    }

    try {
      setIsSubmitting(true)

      toast.success('Voto registrado com sucesso!')

      setVoteOption(null)
    } catch (error) {
      console.error('Erro ao registrar voto:', error)
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

  if (activeAgendas.length === 0) {
    return (
      <Card className="max-w-2xl mx-auto">
        <CardHeader>
          <CardTitle>Nenhuma Sessão Ativa</CardTitle>
          <CardDescription>
            Não há pautas com sessões de votação abertas no momento
          </CardDescription>
        </CardHeader>

        <CardFooter>
          <Button variant="default" className="w-full" onClick={() => {}}>
            Ver Pautas Disponíveis
          </Button>
        </CardFooter>
      </Card>
    )
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
        {activeAgendas.length > 1 && (
          <div className="space-y-2">
            <Label htmlFor="agenda-select">Selecione a Pauta</Label>
            <Select
              value={selectedAgenda?.id || ''}
              onValueChange={(value) => {
                const selected = activeAgendas.find(
                  (agenda) => agenda.id === value,
                )
                if (selected) setSelectedAgenda(selected)
              }}
            >
              <SelectTrigger className="w-full">
                <SelectValue placeholder="Selecione uma pauta" />
              </SelectTrigger>
              <SelectContent>
                {activeAgendas.map((agenda) => (
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

              {timeLeft && (
                <div className="flex items-center mt-2 text-sm font-medium">
                  <Clock className="h-4 w-4 mr-1 text-amber-500" />
                  <span>Tempo restante: {timeLeft}</span>
                </div>
              )}
            </div>

            <Separator />

            <div className="space-y-4">
              <div className="space-y-2">
                <Label htmlFor="member-id">ID do Associado</Label>
                <Input
                  id="member-id"
                  placeholder="Digite seu ID de associado"
                  value={memberId}
                  onChange={(e) => setMemberId(e.target.value)}
                />
              </div>

              <div className="space-y-2">
                <Label htmlFor="cpf">CPF (opcional)</Label>
                <Input
                  id="cpf"
                  placeholder="Digite seu CPF para validação"
                  value={cpf}
                  onChange={(e) => setCpf(e.target.value)}
                />
                <p className="text-xs text-muted-foreground">
                  O CPF é opcional e será usado para validação adicional.
                </p>
              </div>

              <div className="space-y-2">
                <Label>Seu Voto</Label>
                <RadioGroup
                  value={voteOption || ''}
                  onValueChange={(value) =>
                    setVoteOption(value as 'SIM' | 'NAO')
                  }
                >
                  <div className="flex items-center space-x-2">
                    <RadioGroupItem value="SIM" id="vote-yes" />
                    <Label htmlFor="vote-yes">Sim</Label>
                  </div>
                  <div className="flex items-center space-x-2">
                    <RadioGroupItem value="NAO" id="vote-no" />
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
          <Button variant="outline" className="flex-1">
            Voltar
          </Button>
          <Button
            variant="default"
            className="flex-1"
            disabled={isSubmitting || !voteOption || !memberId}
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
