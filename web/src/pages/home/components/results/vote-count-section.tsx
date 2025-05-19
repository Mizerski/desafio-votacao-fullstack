import { Agenda } from '@/shared/types/agenda'

interface VoteCountSectionProps {
  agenda: Agenda
  getYesPercentage: (agenda: Agenda) => number
  getNoPercentage: (agenda: Agenda) => number
}

/**
 * Componente que exibe a contagem de votos de uma agenda
 */
export function VoteCountSection({ 
  agenda, 
  getYesPercentage, 
  getNoPercentage 
}: VoteCountSectionProps) {
  return (
    <div className="space-y-2">
      <h4 className="text-sm font-medium">Contagem de Votos</h4>
      <div className="space-y-1">
        <div className="flex items-center justify-between text-sm">
          <span className="text-muted-foreground">Total de votos:</span>
          <span className="font-semibold">{agenda.totalVotes}</span>
        </div>
        <div className="flex items-center justify-between text-sm">
          <span className="text-green-600">Sim:</span>
          <span className="font-semibold text-green-600">
            {agenda.yesVotes} ({getYesPercentage(agenda)}%)
          </span>
        </div>
        <div className="flex items-center justify-between text-sm">
          <span className="text-red-600">Não:</span>
          <span className="font-semibold text-red-600">
            {agenda.noVotes} ({getNoPercentage(agenda)}%)
          </span>
        </div>
      </div>
    </div>
  )
} 