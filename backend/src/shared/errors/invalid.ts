export class InvalidError extends Error {
  constructor(resource: string) {
    super(`${resource} inválido`)
  }
}
