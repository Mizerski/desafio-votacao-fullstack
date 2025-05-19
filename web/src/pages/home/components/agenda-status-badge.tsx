import { Badge } from '@/components/ui/badge'
import { AgendaStatus } from '@/shared/types/agenda'

interface AgendaStatusBadgeProps {
  status: AgendaStatus
}

/**
 * Componente de badge para exibir o status da agenda
 * @param props - Propriedades do componente
 * @returns Badge com o status da agenda
 */
export function AgendaStatusBadge({ status }: AgendaStatusBadgeProps) {
  if (status === 'OPEN') {
    return (
      <Badge variant="outline">
        Não iniciada
      </Badge>
    )
  }

  if (status === 'FINISHED') {
    return (
      <Badge className="bg-orange-100 text-orange-600 border-orange-400">
        Encerrada
      </Badge>
    )
  }

  if (status === 'IN_PROGRESS') {
    return (
      <Badge className="bg-green-100 text-green-600 border-green-400">
        Em andamento
      </Badge>
    )
  }

  if (status === 'CANCELLED') {
    return (
      <Badge variant="destructive">
        Cancelada
      </Badge>
    )
  }

  return null
} 