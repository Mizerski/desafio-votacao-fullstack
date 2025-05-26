export const ENDPOINT_PREFIX = 'http://localhost:8080/api'

/**
 * @description Endpoint para gerenciar as agendas
 * @example
 * const agenda = await api.get(AGENDA.GET_ALL)
 */
export const AGENDA = {
  GET_ALL: `${ENDPOINT_PREFIX}/v1/agendas`,
  GET_ALL_FINISHED: `${ENDPOINT_PREFIX}/v1/agendas/finished`,
  GET_ALL_OPEN: `${ENDPOINT_PREFIX}/v1/agendas/open`,
  GET_BY_ID: `${ENDPOINT_PREFIX}/v1/agendas/:id`,
  CREATE: `${ENDPOINT_PREFIX}/v1/agendas`,
  START_SESSION: `${ENDPOINT_PREFIX}/v1/agendas/:agendaId/start`,
  FINALIZE: `${ENDPOINT_PREFIX}/v1/agendas/:agendaId/finalize`,
} as const

/**
 * @description Endpoint para gerenciar os votos
 * @example
 * const vote = await api.post(VOTE.CREATE, { agendaId, userId, vote })
 */
export const VOTE = {
  CREATE: `${ENDPOINT_PREFIX}/v1/votes`,
  GET_BY_USER_ID: `${ENDPOINT_PREFIX}/v1/votes/user/:userId`,
  GET_BY_USER_ID_AND_AGENDA_ID: `${ENDPOINT_PREFIX}/v1/votes/user/:userId/agenda/:agendaId`,
  GET_BY_AGENDA_ID: `${ENDPOINT_PREFIX}/v1/votes/agenda/:agendaId`,
} as const

/**
 * @description Endpoint para autenticação
 * @example
 * const login = await api.post(AUTH.LOGIN, { email, password })
 */
export const AUTH = {
  LOGIN: `${ENDPOINT_PREFIX}/auth/login`,
  LOGOUT: `${ENDPOINT_PREFIX}/auth/logout`,
  REGISTER: `${ENDPOINT_PREFIX}/auth/register`,
  GET_USER: `${ENDPOINT_PREFIX}/v1/auth/me`,
  REFRESH_TOKEN: `${ENDPOINT_PREFIX}/auth/refresh`,
  VALIDATE_TOKEN: `${ENDPOINT_PREFIX}/auth/validate`,
} as const

export const ENDPOINTS = {
  AGENDA,
  VOTE,
  AUTH,
} as const
