export type AgendaStatus = 'OPEN' | 'IN_PROGRESS' | 'FINISHED' | 'CANCELLED' | 'ALL'
export type AgendaCategory = 'PROJETOS' | 'ADMINISTRATIVO' | 'ELEICOES' | 'ESTATUTARIO' | 'FINANCEIRO' | 'OUTROS' | 'ALL'
export type AgendaResult = 'APPROVED' | 'REJECTED' | 'TIE' | 'UNVOTED' | 'ALL'

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
