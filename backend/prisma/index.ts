import { PrismaClient } from '@prisma/client'
import { ConstantsEnv } from '@shared/constants/env'

const prismaClient = new PrismaClient({
  log: ConstantsEnv.NODE_ENV === 'development' ? ['error'] : [],
})

export { prismaClient }
