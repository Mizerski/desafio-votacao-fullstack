import { VoteIcon } from 'lucide-react'
import { useState } from 'react'

import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { cn } from '@/components/lib/utils'
import { Label } from '@/components/ui/label'
import { useAuth } from '@/hooks/useAuth'

export function LoginForm({
  className,
  ...props
}: React.ComponentPropsWithoutRef<'div'>) {
  const [email, setEmail] = useState<string>('')
  const [password, setPassword] = useState<string>('')
  const { login, isLoading, error } = useAuth()

  async function handleSubmit(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault()
    await login(email, password)
  }

  return (
    <div className={cn('flex flex-col gap-6', className)} {...props}>
      <form onSubmit={handleSubmit} className="w-full">
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
                value={email}
                onChange={(e) => setEmail(e.target.value)}
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
              value={password}
              onChange={(e) => setPassword(e.target.value)}
            />
          </div>
          {error && (
            <span className="text-sm text-red-500 text-center">{error}</span>
          )}
          <Button type="submit" className="w-full" disabled={isLoading}>
            {isLoading ? 'Carregando...' : 'Acessar'}
          </Button>
        </div>
      </form>
    </div>
  )
}
