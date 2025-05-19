import { Tabs, TabsList, TabsTrigger, TabsContent } from '@/components/ui/tabs'
import { CreateAgenda } from './create-agenda'
import { AgendaList } from './agenda-list/agenda-list'
import { AgendaResults } from './results/results-agenda'
import { AgendaVoting } from './voting-agenda'

export function HomeTabs() {
  return (
    <Tabs defaultValue="agendas" className="w-full">
      <TabsList className="grid w-full grid-cols-4">
        <TabsTrigger value="agendas">Pautas</TabsTrigger>
        <TabsTrigger value="create">Cadastrar Pauta</TabsTrigger>
        <TabsTrigger value="voting">Votação</TabsTrigger>
        <TabsTrigger value="results">Resultados</TabsTrigger>
      </TabsList>

      <TabsContent value="create">
        <CreateAgenda />
      </TabsContent>

      <TabsContent value="agendas">
        <AgendaList />
      </TabsContent>

      <TabsContent value="results">
        <AgendaResults />
      </TabsContent>

      <TabsContent value="voting">
        <AgendaVoting />
      </TabsContent>
    </Tabs>
  )
}
