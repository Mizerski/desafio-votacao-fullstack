/**
 * Enum que representa os possíveis status de uma agenda
 */
export enum AgendaStatus {
  DRAFT = 'DRAFT',
  OPEN = 'OPEN',
  IN_PROGRESS = 'IN_PROGRESS',
  FINISHED = 'FINISHED',
  CANCELLED = 'CANCELLED',
  ALL = 'ALL',
}

/**
 * Interface que define os métodos para manipulação de status da agenda
 */
export interface IAgendaStatusManager {
  canStartSession(status: AgendaStatus): boolean;
  canVote(status: AgendaStatus): boolean;
  canViewResults(status: AgendaStatus): boolean;
  isActive(status: AgendaStatus): boolean;
}

/**
 * Implementação concreta do gerenciador de status da agenda
 */
export class AgendaStatusManager implements IAgendaStatusManager {
  canStartSession(status: AgendaStatus): boolean {
    return status === AgendaStatus.OPEN || status === AgendaStatus.DRAFT;
  }

  canVote(status: AgendaStatus): boolean {
    return status === AgendaStatus.IN_PROGRESS;
  }

  canViewResults(status: AgendaStatus): boolean {
    return status === AgendaStatus.FINISHED;
  }

  isActive(status: AgendaStatus): boolean {
    return status === AgendaStatus.IN_PROGRESS;
  }
}

/**
 * Instância singleton do gerenciador de status
 */
export const agendaStatusManager = new AgendaStatusManager(); 