import { Badge } from '@/components/ui/badge'
import { AgendaStatus } from '@/shared/types/agenda-status'

interface AgendaStatusBadgeProps {
  status: AgendaStatus
}

/**
 * Componente que exibe um badge com o status da agenda
 * @param props - Propriedades do componente
 * @returns Badge com o status da agenda
 */
export function AgendaStatusBadge({ status }: AgendaStatusBadgeProps) {
  const getStatusConfig = (status: AgendaStatus): { label: string; variant: 'default' | 'secondary' | 'destructive' | 'outline' } => {
    switch (status) {
      case AgendaStatus.DRAFT:
        return {
          label: 'Rascunho',
          variant: 'outline'
        }
      case AgendaStatus.OPEN:
        return {
          label: 'Não iniciada',
          variant: 'secondary'
        }
      case AgendaStatus.IN_PROGRESS:
        return {
          label: 'Em andamento',
          variant: 'default'
        }
      case AgendaStatus.FINISHED:
        return {
          label: 'Encerrada',
          variant: 'secondary'
        }
      case AgendaStatus.CANCELLED:
        return {
          label: 'Cancelada',
          variant: 'destructive'
        }
      default:
        return {
          label: 'Status desconhecido',
          variant: 'outline'
        }
    }
  }

  const config = getStatusConfig(status)

  return (
    <Badge variant={config.variant}>
      {config.label}
    </Badge>
  )
} 