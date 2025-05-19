export class AlreadyExistsError extends Error {
  constructor(resource: string) {
    super(`${resource} já existe`)
  }
}
