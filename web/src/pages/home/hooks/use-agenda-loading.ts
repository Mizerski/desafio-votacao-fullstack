import { useState, useCallback } from 'react'

interface UseAgendaLoadingReturn {
  loading: boolean
  error: string | null
  setLoading: (loading: boolean) => void
  setError: (error: string | null) => void
  retry: () => void
}

/**
 * Hook para gerenciar o estado de carregamento e erro das agendas
 * @returns Funções e estados para gerenciar carregamento e erro
 */
export function useAgendaLoading(): UseAgendaLoadingReturn {
  const [loading, setLoading] = useState<boolean>(true)
  const [error, setError] = useState<string | null>(null)

  const retry = useCallback(() => {
    setLoading(true)
    setError(null)
  }, [])

  return {
    loading,
    error,
    setLoading,
    setError,
    retry,
  }
} 