import { VoteIcon } from 'lucide-react'

import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { cn } from '@/components/lib/utils'
import { Label } from '@/components/ui/label'
import { useNavigate } from 'react-router'

export function LoginForm({
  className,
  ...props
}: React.ComponentPropsWithoutRef<'div'>) {
  const navigation = useNavigate()

  function handleSubmit(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault()
    navigation('/home')
  }

  return (
    <div className={cn('flex flex-col gap-6', className)} {...props}>
      <form onSubmit={handleSubmit}>
        <div className="flex flex-col gap-6">
          <div className="flex flex-col items-center gap-2">
            <div className="flex h-8 w-8 items-center justify-center rounded-md">
              <VoteIcon className="size-6" />
            </div>
            <h1 className="text-xl font-bold text-center">
              Bem-vindo ao Sistema de Gerenciamento de Eventos
            </h1>
          </div>

          <div className="flex flex-col">
            <div className="grid gap-2">
              <Label htmlFor="email">Email de acesso</Label>
              <Input
                id="email"
                type="email"
                placeholder="exemplo@email.com"
                required
              />
            </div>
          </div>
          <div className="grid gap-2">
            <Label htmlFor="password">Senha</Label>
            <Input
              id="password"
              type="password"
              placeholder="********"
              required
            />
          </div>
          <Button type="submit" className="w-full">
            Acessar
          </Button>
        </div>
      </form>
    </div>
  )
}
