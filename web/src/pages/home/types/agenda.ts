export type AgendaStatus = 'em_andamento' | 'encerrada' | 'não_iniciada' | 'cancelada' | 'ALL'
export type AgendaCategory = 'Projetos' | 'Administrativo' | 'Eleições' | 'Estatutário' | 'ALL'

export interface Agenda {
  id: string
  title: string
  description: string
  status: AgendaStatus
  category: AgendaCategory
  session?: {
    endTime: string
    startTime: string
  }
} 