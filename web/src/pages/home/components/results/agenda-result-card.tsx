import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'

interface AgendaResultCardProps {
  title: string
  value: number
  percentage: number
  colorScheme: 'yes' | 'no'
}

/**
 * Componente que exibe um card com os dados de votação
 */
export function AgendaResultCard({
  title,
  value,
  percentage,
  colorScheme,
}: AgendaResultCardProps) {
  const colors = {
    yes: {
      bg: 'bg-green-50',
      border: 'border-green-200',
      title: 'text-green-700',
      value: 'text-green-600',
    },
    no: {
      bg: 'bg-red-50',
      border: 'border-red-200',
      title: 'text-red-700',
      value: 'text-red-600',
    },
  }

  const { bg, border, title: titleColor, value: valueColor } = colors[colorScheme]

  return (
    <Card className={`${bg} ${border}`}>
      <CardHeader className="pb-2">
        <CardTitle className={`${titleColor} text-lg`}>{title}</CardTitle>
      </CardHeader>
      <CardContent>
        <div className={`text-4xl font-bold ${valueColor}`}>{value}</div>
        <div className={`text-sm ${titleColor}`}>{percentage}%</div>
      </CardContent>
    </Card>
  )
} 