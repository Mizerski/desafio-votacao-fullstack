/**
 * Erro para indicar que um recurso é inválido
 * @param resource Recurso inválido
 * @example
 * throw new InvalidError('Pauta')
 * // => "Pauta inválida"
 */
export class InvalidError extends Error {
  constructor(resource: string) {
    super(`${resource} inválido`)
  }
}
