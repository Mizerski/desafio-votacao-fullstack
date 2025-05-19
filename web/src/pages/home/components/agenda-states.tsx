import { AlertCircle, Loader2 } from 'lucide-react'
import { Button } from '@/components/ui/button'

interface AgendaLoadingStateProps {
  message?: string
}

/**
 * Componente de estado de carregamento para a lista de agendas
 * @param props - Propriedades do componente
 * @returns Componente de carregamento
 */
export function AgendaLoadingState({ message = "Carregando pautas..." }: AgendaLoadingStateProps) {
  return (
    <div className="flex justify-center items-center h-64">
      <Loader2 className="h-8 w-8 animate-spin text-primary" />
      <span className="ml-2">{message}</span>
    </div>
  )
}

interface AgendaErrorStateProps {
  error: string
  onRetry: () => void
}

/**
 * Componente de estado de erro para a lista de agendas
 * @param props - Propriedades do componente
 * @returns Componente de erro
 */
export function AgendaErrorState({ error, onRetry }: AgendaErrorStateProps) {
  return (
    <div className="flex justify-center items-center h-64 flex-col">
      <AlertCircle className="h-8 w-8 mb-2" />
      <p>{error}</p>
      <Button
        variant="outline"
        className="mt-4"
        onClick={onRetry}
      >
        Tentar novamente
      </Button>
    </div>
  )
}

interface AgendaEmptyStateProps {
  onCreateAgenda: () => void
}

/**
 * Componente de estado vazio para a lista de agendas
 * @param props - Propriedades do componente
 * @returns Componente de estado vazio
 */
export function AgendaEmptyState({ onCreateAgenda }: AgendaEmptyStateProps) {
  return (
    <div className="text-center p-8">
      <h3 className="text-lg font-medium mb-2">Nenhuma pauta cadastrada</h3>
      <p className="text-muted-foreground mb-4">
        Crie uma nova pauta para iniciar uma votação
      </p>
      <Button
        variant="default"
        onClick={onCreateAgenda}
      >
        Cadastrar Pauta
      </Button>
    </div>
  )
} 