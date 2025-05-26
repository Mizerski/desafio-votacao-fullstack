import { Agenda, AgendaUtils } from '@/shared/types/agenda'
import { Tag, Clock, Timer } from 'lucide-react'
import { formatDistance, format } from 'date-fns'
import { ptBR } from 'date-fns/locale'

interface AgendaInfoSectionProps {
  agenda: Agenda
}

/**
 * Componente que exibe as informações básicas de uma agenda
 */
export function AgendaInfoSection({ agenda }: AgendaInfoSectionProps) {
  const startDate = AgendaUtils.getStartDate(agenda)
  const endDate = AgendaUtils.getEndDate(agenda)

  return (
    <div className="space-y-2">
      <h4 className="text-sm font-medium">Informações</h4>
      <div className="space-y-1">
        {/* Categoria */}
        <div className="flex items-center text-sm">
          <Tag className="h-4 w-4 mr-1 text-gray-400" />
          <span className="mr-1 text-muted-foreground">Categoria:</span>
          {agenda.category}
        </div>
        
        {/* Tempo da sessão */}
        {startDate && endDate && (
          <div className="flex items-center text-sm">
            <Timer className="h-4 w-4 mr-1 text-gray-400" />
            <span className="mr-1 text-muted-foreground">Duração:</span>
            {formatDistance(
              new Date(startDate),
              new Date(endDate),
              { locale: ptBR }
            )}
          </div>
        )}
        
        {/* Data de encerramento */}
        {endDate && (
          <div className="flex items-center text-sm">
            <Clock className="h-4 w-4 mr-1 text-gray-400" />
            <span className="mr-1 text-muted-foreground">Encerrada:</span>
            {format(
              new Date(endDate), 
              "dd/MM/yyyy 'às' HH:mm", 
              { locale: ptBR }
            )}
          </div>
        )}
      </div>
    </div>
  )
} 