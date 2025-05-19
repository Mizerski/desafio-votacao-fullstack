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

export function AgendaVoting() {
  const [loading, setLoading] = useState<boolean>(true)
  const [error, setError] = useState<string | null>(null)
  const [selectedAgenda, setSelectedAgenda] = useState<Agenda | null>(null)
  const [memberId, setMemberId] = useState<string>('')
  const [cpf, setCpf] = useState<string>('')
  const [voteOption, setVoteOption] = useState<AgendaVote | null>(null)
  const [isSubmitting, setIsSubmitting] = useState<boolean>(false)

  const { getAllOpenAgenda, openAgendas } = useAgenda()

  useEffect(() => {
    const loadingTimeout = setTimeout(() => {
      setLoading(true)
      getAllOpenAgenda()
      setLoading(false)
    }, 1000)

    return () => clearTimeout(loadingTimeout)
  }, [])

  function handleSubmitVote() {
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
                  onValueChange={(value) => setVoteOption(value as AgendaVote)}
                >
                  <div className="flex items-center space-x-2">
                    <RadioGroupItem value="YES" id="vote-yes" />
                    <Label htmlFor="vote-yes">Sim</Label>
                  </div>
                  <div className="flex items-center space-x-2">
                    <RadioGroupItem value="NO" id="vote-no" />
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
