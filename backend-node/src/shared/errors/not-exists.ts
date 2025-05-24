/**
 * Erro para indicar que um recurso não existe
 * @param resource Recurso não encontrado
 * @example
 * throw new NotExistsError('Pauta')
 * // => "Pauta não encontrada"
 */
export class NotExistsError extends Error {
  constructor(resource: string) {
    super(`${resource} não encontrado`)
  }
}
