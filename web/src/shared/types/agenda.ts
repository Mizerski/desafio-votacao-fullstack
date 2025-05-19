

export interface Agenda {
  id: string
  title: string
  description: string
  status: AgendaStatus
  category: AgendaCategory
  result: AgendaResult
  votes: {
    [key in AgendaVote]: number
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
  ALL = 'ALL',
}

export enum AgendaStatus {
  OPEN = 'OPEN',
  IN_PROGRESS = 'IN_PROGRESS',
  FINISHED = 'FINISHED',
  CANCELLED = 'CANCELLED',
  ALL = 'ALL',
}


export enum AgendaResult {
  APPROVED = 'APPROVED',
  REJECTED = 'REJECTED',
  TIE = 'TIE',
  UNVOTED = 'UNVOTED',
}


export enum AgendaVote {
  YES = 'YES',
  NO = 'NO',
}
