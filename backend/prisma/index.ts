import { PrismaClient } from '@prisma/client'
import { env } from '../src/env'
const prismaClient = new PrismaClient({
  log: env.NODE_ENV === 'development' ? ['error'] : [],
})

export { prismaClient }
