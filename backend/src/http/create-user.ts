import { UserService } from '@services/user-services'
import { AlreadyExistsError } from '@shared/errors/already-exists'
import { FastifyReply, FastifyRequest } from 'fastify'
import { registerBodySchema } from './schemas'
import { PrismaUser } from '@repositories/prisma/prisma-user'

/**
 * Cria um novo usuário com base nos dados fornecidos na requisição.
 */
export async function createUser(request: FastifyRequest, reply: FastifyReply) {
  const { name, email, hashPassword, document } = registerBodySchema.parse(
    request.body,
  )

  try {
    const usersRepository = new PrismaUser()
    const createService = new UserService(usersRepository)

    const userCreated = await createService.execute({
      name,
      email,
      password: hashPassword,
      document,
    })

    reply.code(201).send({
      message: '[CREATED]',
      user: userCreated,
    })
  } catch (error) {
    console.error('[createUser] [ERROR]', error)
    if (error instanceof AlreadyExistsError) {
      return reply.code(409).send({ message: error.message })
    }

    throw error
  }
}
