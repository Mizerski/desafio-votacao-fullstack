import fastify from 'fastify'
import fastifyCors from '@fastify/cors'
import { ascii } from '@shared/constants/ascii'
import {
  serializerCompiler,
  validatorCompiler,
  ZodTypeProvider,
} from 'fastify-type-provider-zod'
import { ConstantsEnv } from '@shared/constants/env'
import { mainRoutes } from '@routes/main'

const server = fastify().withTypeProvider<ZodTypeProvider>()

// ============================== CORS ==============================
server.register(fastifyCors, {
  origin: '*',
  methods: ['GET', 'POST', 'PUT', 'DELETE', 'OPTIONS'],
  credentials: true,
})

// ============================== ROUTES ==============================
server.register(mainRoutes)

// ============================== VALIDATOR ==============================
server.setValidatorCompiler(validatorCompiler)
server.setSerializerCompiler(serializerCompiler)

// ============================== LISTEN ==============================
server.listen({ port: ConstantsEnv.PORT, host: '0.0.0.0' }, (err, address) => {
  if (err) {
    console.error(err)
    process.exit(1)
  }
  console.log(ascii)
  console.log(
    `[${ConstantsEnv.NODE_ENV.toUpperCase()}] Aplicação rodando em -> ${address}`,
  )
})
