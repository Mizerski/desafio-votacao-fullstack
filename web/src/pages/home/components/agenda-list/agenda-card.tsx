import { Clock, Tag, Timer } from 'lucide-react'
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
import { Agenda, AgendaStatus } from '@/shared/types/agenda'
import { AgendaStatusBadge } from '../agenda-status-badge'
import { useTimer } from '@/shared/hooks/use-timer'
import { Progress } from '@/components/ui/progress'

interface AgendaCardProps {
  agenda: Agenda
  isProcessing?: boolean
  onOpenSession: (duration: number) => void
  onViewResults: () => void
  onVote: () => void
}

/**
 * Componente de card para exibir uma agenda
 * @param props - Propriedades do componente
 * @returns Card com as informações da agenda
 */
export function AgendaCard({ 
  agenda, 
  isProcessing = false, 
  onOpenSession, 
  onViewResults, 
  onVote 
}: AgendaCardProps) {
  const isSessionEnded = agenda.endDate && new Date(agenda.endDate) < new Date()
  const { isRunning, formatTime, remainingTime } = useTimer(agenda.endDate)

  const calculateProgress = () => {
    if (!agenda.startDate || !agenda.endDate || !isRunning) return 100

    const startTime = new Date(agenda.startDate).getTime()
    const endTime = new Date(agenda.endDate).getTime()
    const totalDuration = (endTime - startTime) / 1000
    
    return 100 - (remainingTime / totalDuration) * 100
  }

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
        
        {agenda.status === AgendaStatus.IN_PROGRESS && isRunning ? (
          <div className="mt-2 space-y-1">
            <div className="flex items-center justify-between text-sm">
              <div className="flex items-center">
                <Timer className="h-4 w-4 mr-1 text-orange-500" />
                <span className="text-orange-500 font-medium">Votação em andamento</span>
              </div>
              <span className="font-mono font-bold">{formatTime()}</span>
            </div>
            <Progress value={calculateProgress()} className="h-1.5" />
            {agenda.totalVotes !== undefined && (
              <div className="flex justify-between text-xs text-muted-foreground mt-1">
                <span>Total de votos: {agenda.totalVotes}</span>
                <div className="flex gap-2">
                  <span className="text-green-600">Sim: {agenda.yesVotes || 0}</span>
                  <span className="text-red-600">Não: {agenda.noVotes || 0}</span>
                </div>
              </div>
            )}
          </div>
        ) : agenda.endDate ? (
          <div className="flex items-center text-sm text-muted-foreground">
            <Clock className="h-4 w-4 mr-1" />
            {isSessionEnded ? (
              <span>Encerrada  {formatDistanceToNow(new Date(agenda.endDate), { locale: ptBR, addSuffix: true })}</span>
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
              disabled={isProcessing}
              onClick={() => onOpenSession(1)}
            >
              {isProcessing ? 'Iniciando...' : 'Abrir Sessão (1 min)'}
            </Button>
            <Button
              variant="outline"
              className="flex-1"
              disabled={isProcessing}
              onClick={() => onOpenSession(5)}
            >
              {isProcessing ? 'Iniciando...' : 'Abrir (5 min)'}
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