export interface Agenda {
  id: string
  title: string
  description: string
  status: AgendaStatus
  category: AgendaCategory
  result: AgendaResult
  totalVotes?: number
  yesVotes?: number
  noVotes?: number
  isActive?: boolean
  createdAt: string
  updatedAt?: string
  votes?: VoteResponse[]
  sessions?: SessionResponse[]
  userVote?: AgendaVote | null
}

export interface VoteResponse {
  id: string
  agendaId: string
  userId: string
  vote: AgendaVote
  createdAt: string
}

export interface SessionResponse {
  id: string
  startTime: string
  endTime: string
  agendaId: string
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
  DRAFT = 'DRAFT',
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

/**
 * Funções auxiliares para trabalhar com sessões de agenda
 */
export const AgendaUtils = {
  /**
   * Obtém a sessão ativa de uma agenda
   * @param agenda Agenda
   * @returns Sessão ativa ou undefined
   */
  getActiveSession: (agenda: Agenda): SessionResponse | undefined => {
    if (!agenda.sessions || agenda.sessions.length === 0) return undefined
    
    const now = new Date()
    return agenda.sessions.find(session => {
      const startTime = new Date(session.startTime)
      const endTime = new Date(session.endTime)
      return startTime <= now && now <= endTime
    })
  },

  /**
   * Obtém a última sessão de uma agenda
   * @param agenda Agenda
   * @returns Última sessão ou undefined
   */
  getLatestSession: (agenda: Agenda): SessionResponse | undefined => {
    if (!agenda.sessions || agenda.sessions.length === 0) return undefined
    
    return agenda.sessions.reduce((latest, current) => {
      const latestStart = new Date(latest.startTime)
      const currentStart = new Date(current.startTime)
      return currentStart > latestStart ? current : latest
    })
  },

  /**
   * Verifica se a agenda tem uma sessão ativa
   * @param agenda Agenda
   * @returns true se tem sessão ativa
   */
  hasActiveSession: (agenda: Agenda): boolean => {
    return AgendaUtils.getActiveSession(agenda) !== undefined
  },

  /**
   * Obtém a data de início da sessão ativa ou mais recente
   * @param agenda Agenda
   * @returns Data de início ou undefined
   */
  getStartDate: (agenda: Agenda): string | undefined => {
    const activeSession = AgendaUtils.getActiveSession(agenda)
    if (activeSession) return activeSession.startTime
    
    const latestSession = AgendaUtils.getLatestSession(agenda)
    return latestSession?.startTime
  },

  /**
   * Obtém a data de fim da sessão ativa ou mais recente
   * @param agenda Agenda
   * @returns Data de fim ou undefined
   */
  getEndDate: (agenda: Agenda): string | undefined => {
    const activeSession = AgendaUtils.getActiveSession(agenda)
    if (activeSession) return activeSession.endTime
    
    const latestSession = AgendaUtils.getLatestSession(agenda)
    return latestSession?.endTime
  },

  /**
   * Verifica se a sessão da agenda já terminou
   * @param agenda Agenda
   * @returns true se a sessão terminou
   */
  isSessionEnded: (agenda: Agenda): boolean => {
    const endDate = AgendaUtils.getEndDate(agenda)
    if (!endDate) return false
    
    return new Date(endDate) < new Date()
  }
}
