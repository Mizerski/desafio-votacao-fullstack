import { Clock, Tag } from 'lucide-react'
import { formatDistanceToNow } from "date-fns"
import { ptBR } from "date-fns/locale"
import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Agenda } from '@/shared/types/agenda'
import { AgendaStatusBadge } from './agenda-status-badge'

interface AgendaCardProps {
  agenda: Agenda
  onOpenSession: (duration: number) => void
  onViewResults: () => void
  onVote: () => void
}

/**
 * Componente de card para exibir uma agenda
 * @param props - Propriedades do componente
 * @returns Card com as informações da agenda
 */
export function AgendaCard({ agenda, onOpenSession, onViewResults, onVote }: AgendaCardProps) {
  const isSessionEnded = agenda.endDate && new Date(agenda.endDate) < new Date()

  return (
    <Card className="overflow-hidden">
      <CardHeader className="pb-2">
        <div className="flex justify-between items-start">
          <CardTitle className="text-lg">{agenda.title}</CardTitle>
          <AgendaStatusBadge status={agenda.status} />
        </div>
        <CardDescription className="line-clamp-2">
          {agenda.description}
        </CardDescription>
      </CardHeader>
      <CardContent className="pb-2">
        <div className="flex items-center text-sm">
          <Tag className="h-4 w-4 mr-1" />
          <span className="mr-2">Categoria:</span>
          {agenda.category}
        </div>
        {agenda.endDate ? (
          <div className="flex items-center text-sm text-muted-foreground">
            <Clock className="h-4 w-4 mr-1" />
            {isSessionEnded ? (
              <span>Encerrada há {formatDistanceToNow(new Date(agenda.endDate), { locale: ptBR, addSuffix: true })}</span>
            ) : (
              <span>Encerra em {formatDistanceToNow(new Date(agenda.endDate), { locale: ptBR, addSuffix: true })}</span>
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
        {!agenda.endDate ? (
          <div className="flex gap-2 w-full">
            <Button
              variant="default"
              className="flex-1"
              onClick={() => onOpenSession(1)}
            >
              Abrir Sessão (1 min)
            </Button>
            <Button
              variant="outline"
              className="flex-1"
              onClick={() => onOpenSession(5)}
            >
              Abrir (5 min)
            </Button>
          </div>
        ) : isSessionEnded ? (
          <Button
            variant="secondary"
            className="w-full"
            onClick={onViewResults}
          >
            Ver Resultados
          </Button>
        ) : (
          <Button
            variant="default"
            className="w-full"
            onClick={onVote}
          >
            Votar Agora
          </Button>
        )}
      </CardFooter>
    </Card>
  )
} 