import { ReactNode, createContext, useContext, useState } from 'react'
import { Agenda } from '@/shared/types/agenda'

/**
 * Interface do contexto de agenda selecionada
 */
interface SelectedAgendaContextType {
  /**
   * Agenda selecionada na aplicação
   */
  selectedAgenda: Agenda | null
  /**
   * Função para definir a agenda selecionada
   * @param agenda - Agenda a ser selecionada
   */
  setSelectedAgenda: (agenda: Agenda | null) => void
}

/**
 * Contexto para gerenciar a agenda selecionada na aplicação
 */
const SelectedAgendaContext = createContext<SelectedAgendaContextType | undefined>(
  undefined
)

/**
 * Hook para acessar o contexto de agenda selecionada
 * @returns Contexto de agenda selecionada
 */
export function useSelectedAgenda() {
  const context = useContext(SelectedAgendaContext)
  
  if (!context) {
    throw new Error('useSelectedAgenda deve ser usado dentro de um SelectedAgendaProvider')
  }
  
  return context
}

/**
 * Provider para o contexto de agenda selecionada
 * @param children - Componentes filhos
 */
export function SelectedAgendaProvider({ children }: { children: ReactNode }) {
  const [selectedAgenda, setSelectedAgenda] = useState<Agenda | null>(null)
  
  return (
    <SelectedAgendaContext.Provider value={{ selectedAgenda, setSelectedAgenda }}>
      {children}
    </SelectedAgendaContext.Provider>
  )
} 