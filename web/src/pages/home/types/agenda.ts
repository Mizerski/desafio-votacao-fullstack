export type AgendaStatus = 'em_andamento' | 'encerrada' | 'não_iniciada' | 'cancelada' | 'ALL'
export type AgendaCategory = 'Projetos' | 'Administrativo' | 'Eleições' | 'Estatutário'|'Financeiro' | 'ALL'
export type AgendaResult = 'Aprovado' | 'Reprovado' | 'Empate' | 'Em andamento' | 'ALL'
export interface Agenda {
  id: string
  title: string
  description: string
  status: AgendaStatus
  category: AgendaCategory
  result: AgendaResult
  votes: {
    yes: number
    no: number
  }
  session?: {
    endTime: string
    startTime: string
  }
} 