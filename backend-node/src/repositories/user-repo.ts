import { Prisma, User } from '@prisma/client'

export interface UserRepo {
  /**
   * Busca um usuário pelo email
   * @param email Email do usuário
   * @returns Promise<User | null>
   * @memberof UsersRepository
   */
  findByEmail(email: string): Promise<User | null>

  /**
   * Cria um usuário
   * @param data Dados do usuário
   * @returns Promise<User>
   * @memberof UsersRepository
   */
  create(data: Prisma.UserCreateInput): Promise<User>

  /**
   * Busca um usuário pelo ID
   * @param id ID do usuário
   * @returns Promise<User | null>
   * @memberof UsersRepository
   */
  findById(id: string): Promise<User | null>
}
