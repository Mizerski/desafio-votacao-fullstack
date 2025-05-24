import { z } from 'zod'
import 'dotenv/config'

/**
 * Schema de validação das variáveis de ambiente
 */
const envSchema = z.object({
  NODE_ENV: z.enum(['development', 'production']).default('development'),
  PORT: z.coerce.number().default(8181),
  DATABASE_URL: z.string(),
  POSTGRES_PASSWORD: z.string(),
  POSTGRES_DB: z.string(),
  POSTGRES_USER: z.string(),
  JWT_SECRET: z.string().default('default'),
})

const _env = envSchema.safeParse(process.env)

if (!_env.success) {
  console.error(
    '[envSchema] Variáveis de ambiente inválidas',
    _env.error.format(),
  )
  throw new Error('[ENVIRONMENT] [ERROR] Variáveis de ambiente inválidas')
}

export const ConstantsEnv = _env.data
