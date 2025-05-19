import { prismaClient } from '@prisma/index'
import { Prisma, User } from '@prisma/client'
import { UserRepo } from '@repositories/user-repo'

export class PrismaUser implements UserRepo {
  async findByEmail(email: string): Promise<User | null> {
    const user = await prismaClient.user.findUnique({
      where: {
        email,
      },
    })
    return user
  }

  async create(data: Prisma.UserCreateInput): Promise<User> {
    const user = await prismaClient.user.create({
      data,
    })
    return user
  }

  async findById(id: string): Promise<User | null> {
    const user = await prismaClient.user.findUnique({
      where: { id },
    })
    return user
  }
}
