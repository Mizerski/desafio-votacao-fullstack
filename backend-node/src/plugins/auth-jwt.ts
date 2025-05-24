import fp from 'fastify-plugin'
import fastifyJwt from '@fastify/jwt'
import { FastifyInstance, FastifyReply, FastifyRequest } from 'fastify'
import { ConstantsEnv } from '@shared/constants/env'

/**
 * Plugin Fastify para autenticação JWT.
 *
 * @param {FastifyInstance} fastify - Instância do Fastify.
 */
export default fp(async function (fastify: FastifyInstance) {
  fastify.register(fastifyJwt, {
    secret: ConstantsEnv.JWT_SECRET,
  })

  /**
   * Decora o Fastify com um método de autenticação JWT.
   *
   * @param {FastifyRequest} request - Requisição do Fastify.
   * @param {FastifyReply} reply - Resposta do Fastify.
   */
  fastify.decorate(
    'jwtAuth',
    async function (request: FastifyRequest, reply: FastifyReply) {
      try {
        const token = request.headers.authorization

        if (!token) {
          return reply.code(401).send({
            message: '[UNAUTHORIZED] Token de autorização é obrigatório',
          })
        }

        const bearerToken = token.split(' ')[1]
        if (!bearerToken) {
          return reply
            .code(401)
            .send({ message: '[UNAUTHORIZED] Formato de token inválido' })
        }

        const decoded = fastify.jwt.verify(bearerToken)
        request.user = decoded as {
          sub: string
          email: string
        }
      } catch (error) {
        console.error('[jwtAuth] [ERROR]', error)
        reply
          .code(401)
          .send({ message: '[UNAUTHORIZED] Token inválido ou expirado' })
      }
    },
  )
})
