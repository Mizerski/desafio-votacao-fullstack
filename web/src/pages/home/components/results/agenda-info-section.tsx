import { Agenda } from '@/shared/types/agenda'
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
        {agenda.startDate && agenda.endDate && (
          <div className="flex items-center text-sm">
            <Timer className="h-4 w-4 mr-1 text-gray-400" />
            <span className="mr-1 text-muted-foreground">Duração:</span>
            {formatDistance(
              new Date(agenda.startDate),
              new Date(agenda.endDate),
              { locale: ptBR }
            )}
          </div>
        )}
        
        {/* Data de encerramento */}
        {agenda.endDate && (
          <div className="flex items-center text-sm">
            <Clock className="h-4 w-4 mr-1 text-gray-400" />
            <span className="mr-1 text-muted-foreground">Encerrada:</span>
            {format(
              new Date(agenda.endDate), 
              "dd/MM/yyyy 'às' HH:mm", 
              { locale: ptBR }
            )}
          </div>
        )}
      </div>
    </div>
  )
} 