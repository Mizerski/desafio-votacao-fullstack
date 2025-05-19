import {
  Card,
  CardHeader,
  CardDescription,
  CardTitle,
  CardFooter,
} from '@/components/ui/card'

import { Button } from '@/components/ui/button'

export function EmptyVoting() {
  return (
    <Card className="max-w-2xl mx-auto">
      <CardHeader>
        <CardTitle>Nenhuma Sessão Ativa</CardTitle>
        <CardDescription>
          Não há pautas com sessões de votação abertas no momento
        </CardDescription>
      </CardHeader>

      <CardFooter>
        <Button variant="default" className="w-full" onClick={() => {}}>
          Ver Pautas Disponíveis
        </Button>
      </CardFooter>
    </Card>
  )
}
