import { z } from 'zod'

export const registerBodySchema = z.object({
  name: z.string().min(3, 'Nome inválido!').max(30),
  email: z.string().email('Email inválido!').toLowerCase(),
  hashPassword: z
    .string()
    .min(8, 'Campo senha deve ter no mínimo 8 caracteres!')
    .max(30),
  document: z.string().min(11, 'CPF inválido!').max(14).optional(),
})

export const authenticateBodySchema = z.object({
  email: z.string().email('Email inválido!').toLowerCase(),
  password: z.string().min(8, 'Campo senha deve ter no mínimo 8 caracteres!'),
})

export const authenticateRefreshTokenBodySchema = z.object({
  token: z.string().min(1, 'Token inválido!'),
})
