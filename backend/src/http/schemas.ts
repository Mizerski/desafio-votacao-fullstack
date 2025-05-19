import { AgendaResult, AgendaStatus, AgendaCategory } from '@prisma/client'
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

// ======================== AGENDA =========================

export const agendaBodySchema = z.object({
  title: z.string().min(3, 'Título inválido!').max(100),
  description: z.string().min(3, 'Descrição inválida!').max(255),
  startDate: z.string().min(3, 'Data de início inválida!').max(255),
  endDate: z.string().min(3, 'Data de término inválida!').max(255),
  category: z
    .nativeEnum(AgendaCategory)
    .optional()
    .default(AgendaCategory.OUTROS),
  status: z.nativeEnum(AgendaStatus).optional().default(AgendaStatus.OPEN),
  result: z.nativeEnum(AgendaResult).optional().default(AgendaResult.UNVOTED),
})
