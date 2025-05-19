import { Loader2, AlertCircle } from 'lucide-react'
import { Button } from '@/components/ui/button'

interface LoadingStateProps {
  message?: string
}

/**
 * Componente que exibe o estado de carregamento
 */
export function LoadingState({ message = 'Carregando pautas encerradas...' }: LoadingStateProps) {
  return (
    <div className="flex justify-center items-center h-64">
      <Loader2 className="h-8 w-8 animate-spin text-primary" />
      <span className="ml-2">{message}</span>
    </div>
  )
}

interface ErrorStateProps {
  error: string
  onRetry: () => void
}

/**
 * Componente que exibe o estado de erro
 */
export function ErrorState({ error, onRetry }: ErrorStateProps) {
  return (
    <div className="flex justify-center items-center h-64 flex-col">
      <AlertCircle className="h-8 w-8 text-destructive mb-2" />
      <p className="text-destructive">{error}</p>
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