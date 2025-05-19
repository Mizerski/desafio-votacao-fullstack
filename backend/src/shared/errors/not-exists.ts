export class NotExistsError extends Error {
  constructor(resource: string) {
    super(`${resource} não encontrado`)
  }
}
