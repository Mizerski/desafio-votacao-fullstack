'use client'

import { useState, useEffect } from 'react'
import { Avatar, AvatarFallback } from '@/components/ui/avatar'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuGroup,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu'
import { Button } from '@/components/ui/button'
import { LogIn, LogOut, Calendar, Shield } from 'lucide-react'
import { format } from 'date-fns'
import { ptBR } from 'date-fns/locale'

interface UserInfo {
  id: string
  firstName: string
  lastName: string
  email: string
  joinedAt: string
}

export  function UserAvatar() {
  const [isLoggedIn, setIsLoggedIn] = useState(false)
  const [user, setUser] = useState<UserInfo | null>(null)

  const mockUser: UserInfo = {
    id: '1',
    firstName: 'John',
    lastName: 'Doe',
    email: 'john.doe@example.com',
    joinedAt: '2024-01-01',
  }
  // Verificar status de login e informações do usuário
  useEffect(() => {
    const checkLoginStatus = () => {
      const loggedIn = true
      setIsLoggedIn(loggedIn)

      if (loggedIn) {
        setUser(mockUser)
      } else {
        setUser(null)
      }
    }

    checkLoginStatus()

    // Verificar status de login a cada 2 segundos (para detectar mudanças)
    const loginInterval = setInterval(checkLoginStatus, 2000)

    return () => clearInterval(loginInterval)
  }, [])

  const handleLogin = () => {

    if (true) {
      setIsLoggedIn(true)
      setUser(mockUser)
    }
  }

  const handleLogout = () => {
    setIsLoggedIn(false)
    setUser(null)
  }

  // Gerar iniciais do nome do usuário
  const getInitials = () => {
    if (!user) return '?'
    return `${user.firstName.charAt(0)}${user.lastName.charAt(0)}`.toUpperCase()
  }



  // Formatar data de ingresso
  const formatJoinDate = (dateString: string) => {
    try {
      return format(new Date(dateString), "dd 'de' MMMM 'de' yyyy", {
        locale: ptBR,
      })
    } catch (e) {
      return dateString
    }
  }


  if (!isLoggedIn) {
    return (
      <Button
        variant="outline"
        size="sm"
        className=" absolute top-4 right-4"
        onClick={handleLogin}
      >
        <LogIn className="h-4 w-4 mr-1" />
        <span className="hidden sm:inline-block">Entrar</span>
      </Button>
    )
  }

  return (
    <div className="absolute top-4 right-4">
      <DropdownMenu>
        <DropdownMenuTrigger asChild>
          <Button
            variant="ghost"
            className="relative h-15 w-15 rounded-full p-8 bg-gray-800"
          >
            <Avatar className={`h-15 w-15 `}>
              <AvatarFallback className=" font-medium">
                {getInitials()}
              </AvatarFallback>
            </Avatar>
          </Button>
        </DropdownMenuTrigger>
        <DropdownMenuContent
          className="w-64 "
          align="end"
        >
          <DropdownMenuLabel>
            <div className="flex flex-col space-y-1">
              <p className="text-base font-medium">
                {user?.firstName} {user?.lastName}
              </p>
              <p className="text-xs text-gray-400">{user?.email}</p>
            </div>
          </DropdownMenuLabel>
          <DropdownMenuSeparator />
          <DropdownMenuGroup>
           
            <DropdownMenuItem className="flex items-center gap-2 cursor-default">
              <Calendar className="h-4 w-4 text-gray-400" />
              <span className="text-xs">
                Membro desde: {user ? formatJoinDate(user.joinedAt) : ''}
              </span>
            </DropdownMenuItem>
          </DropdownMenuGroup>
          <DropdownMenuSeparator />
          <DropdownMenuItem
            className="flex items-center gap-2 text-red-400 focus:text-red-400 focus:bg-red-900/20"
            onClick={handleLogout}
          >
            <LogOut className="h-4 w-4" />
            <span>Sair</span>
          </DropdownMenuItem>
        </DropdownMenuContent>
      </DropdownMenu>
    </div>
  )
}
