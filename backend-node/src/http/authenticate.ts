import { InvalidError } from '@shared/errors/invalid'
import { FastifyReply, FastifyRequest } from 'fastify'
import {
  authenticateBodySchema,
  authenticateRefreshTokenBodySchema,
} from './schemas'
import { AuthenticateService } from '@services/authenticate'
import { PrismaUser } from '@repositories/prisma/prisma-user'

export async function authenticate(
  request: FastifyRequest,
  reply: FastifyReply,
) {
  const { email, password } = authenticateBodySchema.parse(request.body)

  try {
    const usersRepository = new PrismaUser()
    const authenticateService = new AuthenticateService(usersRepository)

    const userAuthenticated = await authenticateService.authenticate({
      email,
      password,
      reply,
    })
    return reply.code(200).send({ ...userAuthenticated })
  } catch (error) {
    console.error('[authenticate] [ERROR]', error)
    if (error instanceof InvalidError) {
      return reply.code(400).send({ message: error.message })
    }

    throw error
  }
}

export async function refreshTokens(
  request: FastifyRequest,
  reply: FastifyReply,
) {
  try {
    const { token } = authenticateRefreshTokenBodySchema.parse(request.body)

    const usersRepository = new PrismaUser()
    const authenticateService = new AuthenticateService(usersRepository)

    const { accessToken, refreshToken } =
      await authenticateService.refreshTokens({
        reply,
        token,
      })

    return reply.code(200).send({ accessToken, refreshToken })
  } catch (error) {
    console.error('[refreshTokens] [ERROR]', error)
    if (error instanceof InvalidError) {
      return reply.code(400).send({ message: error.message })
    }

    throw error
  }
}
