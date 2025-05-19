import '@fastify/jwt'
declare module '@fastify/jwt' {
  interface FastifyJWT {
    user: {
      email: string
      sub: string
    }
  }
}
