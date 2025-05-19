import { ReactNode, createContext, useContext, useState } from 'react'

type TabValue = 'agendas' | 'create' | 'voting' | 'results'

/**
 * Interface do contexto de abas
 */
interface TabsContextType {
  /**
   * Valor atual da aba selecionada
   */
  activeTab: TabValue
  /**
   * Função para alterar a aba ativa
   * @param tab - Aba a ser ativada
   */
  setActiveTab: (tab: TabValue) => void
}

/**
 * Contexto para gerenciar as abas da aplicação
 */
const TabsContext = createContext<TabsContextType | undefined>(undefined)

/**
 * Hook para acessar o contexto de abas
 * @returns Contexto de abas
 */
export function useTabsContext() {
  const context = useContext(TabsContext)
  
  if (!context) {
    throw new Error('useTabsContext deve ser usado dentro de um TabsProvider')
  }
  
  return context
}

/**
 * Provider para o contexto de abas
 * @param children - Componentes filhos
 */
export function TabsProvider({ children }: { children: ReactNode }) {
  const [activeTab, setActiveTab] = useState<TabValue>('agendas')
  
  return (
    <TabsContext.Provider value={{ activeTab, setActiveTab }}>
      {children}
    </TabsContext.Provider>
  )
} 