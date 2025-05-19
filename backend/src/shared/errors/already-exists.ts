/**
 * Erro para indicar que um recurso já existe
 * @param resource Recurso que já existe
 * @example
 * throw new AlreadyExistsError('Usuário')
 * // => "Usuário já existe"
 */
export class AlreadyExistsError extends Error {
  constructor(resource: string) {
    super(`${resource} já existe`)
  }
}
