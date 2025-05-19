import { PrismaClient } from '@prisma/client'
import { hash } from 'bcrypt'

const prisma = new PrismaClient()

/**
 * Função principal para popular o banco de dados com dados iniciais
 */
async function main() {
  // Criando um usuário de teste
  const passwordHash = await hash('123456', 8)

  const user = await prisma.user.upsert({
    where: { email: 'teste@exemplo.com' },
    update: {},
    create: {
      name: 'Usuário Teste',
      email: 'teste@exemplo.com',
      password: passwordHash,
      document: '12345678900',
    },
  })

  console.log('Usuário de teste criado:', user)
}

main()
  .catch((e) => {
    console.error(e)
    process.exit(1)
  })
  .finally(async () => {
    await prisma.$disconnect()
  })
