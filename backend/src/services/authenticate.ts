import bcrypt from 'bcrypt'

import { FastifyReply } from 'fastify'
import { NotExistsError } from '@shared/errors/not-exists'
import { UserRepo } from '@repositories/user-repo'
import { InvalidError } from '@shared/errors/invalid'

interface AuthenticateServiceRequest {
  email: string
  password: string
  reply: FastifyReply
}

interface UserAuthenticated {
  id: string
  name: string
  email: string
  document?: string | null
}

interface RefreshTokens {
  accessToken: string
  refreshToken: string
}

interface TokenInfo {
  payload: {
    email: string
  }
  sign: { sub: string; expiresIn: string }
}

/**
 * Serviço de autenticação de usuário
 * @param usersRepository - Repositório de usuários
 */
export class AuthenticateService {
  constructor(private usersRepository: UserRepo) {}

  /**
   * Autentica um usuário com base no email e senha fornecidos
   * @param email - Email do usuário
   * @param password - Senha do usuário
   * @param reply - Resposta do Fastify
   * @returns - Retorna o usuário autenticado e os tokens de acesso e refresh
   * @throws InvalidError - Se as credenciais forem inválidas
   */
  async authenticate({
    email,
    password,
    reply,
  }: AuthenticateServiceRequest): Promise<UserAuthenticated & RefreshTokens> {
    const user = await this.usersRepository.findByEmail(email)

    if (!user) throw new InvalidError('Usuário')

    const doesPasswordMatch = await bcrypt.compare(password, user.password)

    if (!doesPasswordMatch) throw new InvalidError('Usuário')

    return this.handleBuildAuthenticateServiceResponse({
      userAuthenticated: user,
      reply,
    })
  }

  /**
   * Atualiza os tokens de acesso e refresh
   * @param token - Token de refresh
   * @param reply - Resposta do Fastify
   * @returns - Retorna os novos tokens de acesso e refresh
   * @throws InvalidCredentialsError - Se as credenciais forem inválidas
   */
  async refreshTokens({
    token,
    reply,
  }: {
    token: string
    reply: FastifyReply
  }): Promise<RefreshTokens> {
    const decoded = reply.server.jwt.verify(token) as {
      email: string
    }
    const { email } = decoded

    const user = await this.usersRepository.findByEmail(email)

    if (!user) throw new InvalidError('Usuário')

    const [accessToken, refreshToken] = await Promise.all([
      this.generateToken({
        reply,
        tokenInfo: {
          payload: {
            email: user.email,
          },
          sign: { sub: user.id, expiresIn: '1d' },
        },
      }),
      this.generateToken({
        reply,
        tokenInfo: {
          payload: {
            email: user.email,
          },
          sign: { sub: user.id, expiresIn: '10d' },
        },
      }),
    ])

    return { accessToken, refreshToken }
  }

  /**
   * Encontra o usuário logado pelo email
   * @param email - Email do usuário
   * @returns - Retorna o usuário encontrado
   * @throws ResourceNotExistsError - Se o usuário não for encontrado
   */
  async findUserAuthenticated({ email }: { email: string }) {
    const user = await this.usersRepository.findByEmail(email)

    if (!user) throw new NotExistsError('Usuário')

    return user
  }

  /**
   * Gera um novo token JWT
   * @param tokenInfo - Informações do token
   * @param reply - Resposta do Fastify
   * @returns - Retorna o token gerado
   */
  generateToken = async ({
    tokenInfo: { payload, sign },
    reply,
  }: {
    tokenInfo: TokenInfo
    reply: FastifyReply
  }) => {
    return reply.jwtSign(
      { ...payload },
      {
        sign,
      },
    )
  }

  /**
   * Constrói a resposta do serviço de autenticação
   * @param type - Tipo de usuário autenticado
   * @param userAuthenticated - Usuário autenticado
   * @param reply - Resposta do Fastify
   * @returns - Retorna o usuário autenticado e os tokens de acesso e refresh
   */
  async handleBuildAuthenticateServiceResponse({
    userAuthenticated: { email, id, name, document },
    reply,
  }: {
    reply: FastifyReply
    userAuthenticated: UserAuthenticated
  }): Promise<UserAuthenticated & RefreshTokens> {
    const [accessToken, refreshToken] = await Promise.all([
      this.generateToken({
        reply,
        tokenInfo: {
          payload: {
            email,
          },
          sign: { sub: id, expiresIn: '1d' },
        },
      }),
      this.generateToken({
        reply,
        tokenInfo: {
          payload: {
            email,
          },
          sign: { sub: id, expiresIn: '10d' },
        },
      }),
    ])
    return {
      email,
      id,
      name,
      document,
      accessToken,
      refreshToken,
    }
  }
}
