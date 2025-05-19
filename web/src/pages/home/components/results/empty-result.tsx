import { Button } from '@/components/ui/button'
import {
  Card,
  CardContent,
  CardFooter,
  CardHeader,
  CardTitle,
  CardDescription,
} from '@/components/ui/card'
import { BarChart2 } from 'lucide-react'

export function EmptyResult() {
  return (
    <Card className="max-w-2xl mx-auto">
      <CardHeader>
        <CardTitle>Nenhuma Votação Encerrada</CardTitle>
        <CardDescription>
          Não há pautas com votações encerradas para exibir resultados
        </CardDescription>
      </CardHeader>
      <CardContent>
        <div className="flex flex-col items-center justify-center py-8">
          <BarChart2 className="h-16 w-16 text-muted-foreground mb-4" />
          <p className="text-center text-muted-foreground">
            Os resultados serão exibidos aqui quando houver votações encerradas.
          </p>
        </div>
      </CardContent>
      <CardFooter>
        <Button variant="default" className="w-full" onClick={() => {}}>
          Ver Pautas Disponíveis
        </Button>
      </CardFooter>
    </Card>
  )
}
