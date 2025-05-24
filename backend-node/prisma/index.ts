import { PrismaClient } from '@prisma/client'
import { ConstantsEnv } from '@shared/constants/env'

/**
 * Cliente Prisma
 * @description
 * Este é o cliente Prisma que é usado para interagir com o banco de dados.
 * Ele é responsável por criar, ler, atualizar e deletar dados na base de dados.
 */
const prismaClient = new PrismaClient({
  log: ConstantsEnv.NODE_ENV === 'development' ? ['error'] : [],
})

export { prismaClient }
