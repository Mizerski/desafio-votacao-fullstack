'use client'

import { Avatar, AvatarFallback } from '@/components/ui/avatar'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu'
import { Button } from '@/components/ui/button'
import { LogOut, MailIcon, User2Icon } from 'lucide-react'
import { useAuth } from '@/shared/hooks/use-auth'

export function UserAvatar() {
  const { user, logout } = useAuth()

  const handleLogout = () => {
    logout()
  }

  const getInitials = () => {
    if (!user) return '?'
    return `${user.name.charAt(0)}`.toUpperCase()
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
        <DropdownMenuContent className="w-64 " align="end">
          <DropdownMenuLabel>
            <div className="flex flex-col space-y-1">
              <div className="flex items-center gap-2">
                <User2Icon className="h-4 w-4 text-gray-400" />
                <p className="text-base font-medium">{user?.name}</p>
              </div>

              <div className="flex items-center gap-2">
                <MailIcon className="h-4 w-4 text-gray-400" />
                <p className="text-xs text-gray-400">{user?.email}</p>
              </div>
            </div>
          </DropdownMenuLabel>

          <DropdownMenuSeparator />
          <DropdownMenuItem
            className="flex items-center gap-2 text-red-400 focus:text-red-400 focus:bg-red-900/20"
            onClick={handleLogout}
          >
            <LogOut className="h-4 w-4 text-red-400" />
            <span>Sair</span>
          </DropdownMenuItem>
        </DropdownMenuContent>
      </DropdownMenu>
    </div>
  )
}
