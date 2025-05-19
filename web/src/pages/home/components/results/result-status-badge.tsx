import { AlertCircle, CheckCircle, XCircle } from 'lucide-react'
import { Agenda, AgendaResult } from '@/shared/types/agenda'
import { Badge } from '@/components/ui/badge'

interface ResultStatusBadgeProps {
  agenda: Agenda
}

/**
 * Retorna o ícone, cor e texto do resultado da votação
 */
function getResultIconAndColor(agenda: Agenda) {
  switch (agenda.result) {
    case AgendaResult.APPROVED:
      return { 
        icon: <CheckCircle className="h-5 w-5 text-green-500 mr-2" />,
        bgColor: 'bg-green-100',
        textColor: 'text-green-800',
        label: 'APROVADA'
      }
    case AgendaResult.REJECTED:
      return { 
        icon: <XCircle className="h-5 w-5 text-red-500 mr-2" />,
        bgColor: 'bg-red-100',
        textColor: 'text-red-800',
        label: 'REJEITADA'
      }
    case AgendaResult.TIE:
      return { 
        icon: <AlertCircle className="h-5 w-5 text-yellow-500 mr-2" />,
        bgColor: 'bg-yellow-100',
        textColor: 'text-yellow-800',
        label: 'EMPATE'
      }
    default:
      return { 
        icon: <AlertCircle className="h-5 w-5 text-gray-500 mr-2" />,
        bgColor: 'bg-gray-100',
        textColor: 'text-gray-800',
        label: 'SEM VOTOS'
      }
  }
}

/**
 * Componente que exibe um badge com o status do resultado da votação
 */
export function ResultStatusBadge({ agenda }: ResultStatusBadgeProps) {
  const resultStyle = getResultIconAndColor(agenda)

  return (
    <Badge className={`${resultStyle.bgColor} ${resultStyle.textColor}`}>
      <div className="flex items-center">
        {resultStyle.icon}
        {resultStyle.label}
      </div>
    </Badge>
  )
} 