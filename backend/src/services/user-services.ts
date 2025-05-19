import { User, Prisma } from '@prisma/client'
import { UserRepo } from '@repositories/user-repo'
import { AlreadyExistsError } from '@shared/errors/already-exists'
import bcrypt from 'bcrypt'

export class UserService {
  constructor(private readonly userRepo: UserRepo) {}

  /**
   * Cria um usuário
   * @param data Dados do usuário
   * @returns Promise<{ user: User }>
   */
  async execute(data: Prisma.UserCreateInput): Promise<{ user: User }> {
    const isEmailInUse = await this.userRepo.findByEmail(data.email)

    if (isEmailInUse) {
      throw new AlreadyExistsError('Usuário')
    }

    const hashedPassword = await bcrypt.hash(data.password, 6)

    const user = await this.userRepo.create({
      ...data,
      password: hashedPassword,
    })

    return { user }
  }
}
