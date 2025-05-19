import { Agenda } from '@/shared/types/agenda'
import { AgendaResultCard } from './agenda-result-card'

interface VotesVisualizationProps {
  agenda: Agenda
  getYesPercentage: (agenda: Agenda) => number
  getNoPercentage: (agenda: Agenda) => number
}

/**
 * Componente que exibe a visualização gráfica dos votos
 */
export function VotesVisualization({
  agenda,
  getYesPercentage,
  getNoPercentage,
}: VotesVisualizationProps) {
  const yesPercentage = getYesPercentage(agenda)
  const noPercentage = getNoPercentage(agenda)

  return (
    <div className="space-y-6">
      <h4 className="font-medium">Resultado da Votação</h4>
      
      <div className="grid grid-cols-2 gap-4">
        <AgendaResultCard
          title="Votos SIM"
          value={agenda.yesVotes || 0}
          percentage={yesPercentage}
          colorScheme="yes"
        />

        <AgendaResultCard
          title="Votos NÃO"
          value={agenda.noVotes || 0}
          percentage={noPercentage}
          colorScheme="no"
        />
      </div>

      <div className="space-y-2">
        <div className="flex justify-between items-center">
          <span className="text-xs font-semibold text-green-600">
            {agenda.yesVotes || 0} votos ({yesPercentage}%)
          </span>
          <span className="text-xs font-semibold text-red-600">
            {agenda.noVotes || 0} votos ({noPercentage}%)
          </span>
        </div>
        <div className="flex h-3 rounded-full overflow-hidden">
          <div 
            className="bg-green-500" 
            style={{ width: `${yesPercentage}%` }} 
          />
          <div 
            className="bg-red-500" 
            style={{ width: `${noPercentage}%` }} 
          />
        </div>
      </div>
    </div>
  )
} 