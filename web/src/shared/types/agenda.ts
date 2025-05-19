

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
  startDate: string
  endDate: string
}



export enum AgendaCategory {
  PROJETOS = 'PROJETOS',
  ADMINISTRATIVO = 'ADMINISTRATIVO',
  ELEICOES = 'ELEICOES',
  ESTATUTARIO = 'ESTATUTARIO',
  FINANCEIRO = 'FINANCEIRO',
  OUTROS = 'OUTROS',
}

export enum AgendaStatus {
  OPEN = 'OPEN',
  IN_PROGRESS = 'IN_PROGRESS',
  FINISHED = 'FINISHED',
  CANCELLED = 'CANCELLED',
}


export enum AgendaResult {
  APPROVED = 'APPROVED',
  REJECTED = 'REJECTED',
  TIE = 'TIE',
  UNVOTED = 'UNVOTED',
}
