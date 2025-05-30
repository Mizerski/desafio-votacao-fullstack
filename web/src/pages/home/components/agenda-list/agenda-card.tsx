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
import { Agenda } from '@/shared/types/agenda'
import { agendaStatusManager } from '@/shared/types/agenda-status'
import { AgendaStatusBadge } from '../agenda-status-badge'
import { useTimer } from '@/shared/hooks/use-timer'
import { Progress } from '@/components/ui/progress'
import { AgendaUtils } from '@/shared/types/agenda'

interface AgendaCardProps {
  agenda: Agenda
  isProcessing?: boolean
  onOpenSession: (duration: number) => void
  onViewResults: () => void
  onVote: () => void
  onTimerEnd?: () => void
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
  onVote,
  onTimerEnd
}: AgendaCardProps) {
  const endDate = AgendaUtils.getEndDate(agenda)
  const startDate = AgendaUtils.getStartDate(agenda)
  const isSessionEnded = AgendaUtils.isSessionEnded(agenda)
  const { isRunning, formatTime } = useTimer(endDate, onTimerEnd)

  const calculateProgress = () => {
    if (!startDate || !endDate || !isRunning) return 100

    const startTime = new Date(startDate).getTime()
    const endTime = new Date(endDate).getTime()
    const currentTime = new Date().getTime()
    const totalDuration = endTime - startTime
    const elapsed = currentTime - startTime

    return Math.max(0, Math.min(100, (elapsed / totalDuration) * 100))
  }

  /**
   * Verifica se o usuário já votou nesta agenda
   */
  const hasUserVoted = agenda.userVote !== null && agenda.userVote !== undefined

  /**
   * Renderiza a seção de informações de tempo da sessão
   */
  const renderSessionTimeInfo = () => {
    // Se a agenda está em progresso e o timer está rodando
    if (agendaStatusManager.isActive(agenda.status) && isRunning) {
      return (
        <div className="mt-2 space-y-1">
          <div className="flex items-center justify-between text-sm">
            <div className="flex items-center">
              <Timer className="h-4 w-4 mr-1 text-orange-500" />
              <span className="text-orange-500 font-medium">Votação em andamento</span>
            </div>
            <span className="font-mono font-bold text-orange-600">{formatTime()}</span>
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
          {hasUserVoted && (
            <div className="flex items-center text-xs text-muted-foreground mt-1">
              <span className="text-blue-600 font-medium">
                ✓ Você votou: {agenda.userVote === 'YES' ? 'Sim' : 'Não'}
              </span>
            </div>
          )}
        </div>
      )
    }

    // Se tem data de fim mas a sessão já terminou
    if (endDate && isSessionEnded) {
      return (
        <div className="mt-2 space-y-1">
          <div className="flex items-center text-sm text-muted-foreground">
            <Clock className="h-4 w-4 mr-1" />
            <span>Encerrada {formatDistanceToNow(new Date(endDate), { locale: ptBR, addSuffix: true })}</span>
          </div>
          {hasUserVoted && (
            <div className="flex items-center text-xs text-muted-foreground">
              <span className="text-blue-600 font-medium">
                ✓ Você votou: {agenda.userVote === 'YES' ? 'Sim' : 'Não'}
              </span>
            </div>
          )}
        </div>
      )
    }

    // Se tem data de fim mas ainda não terminou
    if (endDate && !isSessionEnded) {
      return (
        <div className="flex items-center text-sm text-muted-foreground mt-2">
          <Clock className="h-4 w-4 mr-1" />
          <span>Encerra em {formatDistanceToNow(new Date(endDate), { locale: ptBR, addSuffix: true })}</span>
        </div>
      )
    }

    // Se não tem sessão iniciada
    return (
      <div className="flex items-center text-sm text-muted-foreground mt-2">
        <Clock className="h-4 w-4 mr-1" />
        <span>Sessão não iniciada</span>
      </div>
    )
  }

  /**
   * Renderiza os botões de ação baseado no status da agenda
   */
  const renderActionButtons = () => {
    // Se a agenda está finalizada ou a sessão terminou, mostra botão de resultados
    if (agendaStatusManager.canViewResults(agenda.status) || isSessionEnded) {
      return (
        <Button
          variant="secondary"
          className="w-full"
          onClick={onViewResults}
        >
          Ver Resultados
        </Button>
      )
    }

    // Se a agenda pode iniciar sessão e não tem sessão iniciada
    if (agendaStatusManager.canStartSession(agenda.status) && !isSessionEnded) {
      return (
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
      )
    }

    // Se o usuário já votou
    if (hasUserVoted) {
      return (
        <Button
          variant="outline"
          className="w-full"
          disabled
        >
          ✓ Voto Registrado ({agenda.userVote === 'YES' ? 'Sim' : 'Não'})
        </Button>
      )
    }

    // Se a agenda está em votação e o usuário ainda não votou
    if (agendaStatusManager.canVote(agenda.status) && !hasUserVoted) {
      return (
        <Button
          variant="default"
          className="w-full"
          onClick={onVote}
        >
          Votar
        </Button>
      )
    }

    return null
  }

  return (
    <Card className="w-full">
      <CardHeader>
        <div className="flex items-start justify-between">
          <div className="space-y-1">
            <CardTitle>{agenda.title}</CardTitle>
            <CardDescription>{agenda.description}</CardDescription>
          </div>
          <AgendaStatusBadge status={agenda.status} />
        </div>
      </CardHeader>
      <CardContent>
        <div className="flex items-center space-x-1">
          <Tag className="h-4 w-4" />
          <span className="text-sm text-muted-foreground">{agenda.category}</span>
        </div>
        {renderSessionTimeInfo()}
      </CardContent>
      <CardFooter>
        {renderActionButtons()}
      </CardFooter>
    </Card>
  )
} 