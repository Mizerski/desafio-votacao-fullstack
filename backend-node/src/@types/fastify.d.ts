import 'fastify'

declare module 'fastify' {
  interface FastifyInstance {
    jwtAuth: (request: FastifyRequest, reply: FastifyReply) => Promise<void>
  }
}
