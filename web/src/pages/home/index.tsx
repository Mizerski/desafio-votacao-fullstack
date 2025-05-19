import { HomeTabs } from './components/tabs'
import { UserAvatar } from './components/user-avatar'
import { TabsProvider } from './contexts/tabs-context'
import { SelectedAgendaProvider } from './contexts/selected-agenda-context'

export function HomePage() {
  return (
    <div className="container mx-auto py-8 px-4">
      <UserAvatar />

      <header className="mb-8 text-center">
        <h1 className="text-3xl font-bold mb-2 ">
          Sistema de Votação Cooperativa
        </h1>
        <p className="text-gray-400">
          Gerencie pautas, sessões de votação e acompanhe resultados
        </p>
      </header>

      <TabsProvider>
        <SelectedAgendaProvider>
          <HomeTabs />
        </SelectedAgendaProvider>
      </TabsProvider>
    </div>
  )
}
