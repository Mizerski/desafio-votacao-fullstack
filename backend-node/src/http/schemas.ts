import {
  AgendaResult,
  AgendaStatus,
  AgendaCategory,
  VoteType,
} from '@prisma/client'
import { z } from 'zod'

// ======================== AUTHENTICATION =========================

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

// ======================== VOTES =========================

export const voteBodySchema = z.object({
  agendaId: z.string().min(1, 'ID da pauta é obrigatório'),
  userId: z.string().min(1, 'ID do usuário é obrigatório'),
  vote: z.enum(['YES', 'NO'], {
    required_error: 'Voto é obrigatório',
    invalid_type_error: 'Voto deve ser YES ou NO',
  }),
})

// ======================== SESSION =========================

export const startSessionBodySchema = z.object({
  agendaId: z.string().min(1, 'Pauta inválida!'),
  durationInMinutes: z.number().min(1, 'Duração inválida!'),
})
