import {
  PrismaClient,
  AgendaCategory,
  AgendaStatus,
  AgendaResult,
} from '@prisma/client'
import { hash } from 'bcrypt'

const prisma = new PrismaClient()

/**
 * @description
 * Esta função é responsável por popular o banco de dados com dados iniciais
 * para teste e desenvolvimento.
 *
 * @returns {Promise<void>}
 * @throws {Error} Se ocorrer um erro ao popular o banco de dados
 */
async function main() {
  const passwordHash = await hash('123456', 8)

  const user = await prisma.user.upsert({
    where: { email: 'acesso@email.com' },
    update: {},
    create: {
      name: 'Acesso Tester',
      email: 'acesso@email.com',
      password: passwordHash,
      document: '12345678900',
    },
  })

  console.log('Usuário de teste criado:', user)

  // PROJETOS
  await prisma.agenda.upsert({
    where: { id: 'proj-001' },
    update: {},
    create: {
      id: 'proj-001',
      title: 'Implantação do Sistema de Energia Solar',
      description:
        'Proposta para instalação de painéis solares no prédio principal, visando reduzir em 40% o consumo de energia elétrica e promover sustentabilidade. O projeto inclui estudo de viabilidade, orçamentos de três fornecedores e cronograma de implementação.',
      status: AgendaStatus.OPEN,
      category: AgendaCategory.PROJETOS,
      result: AgendaResult.UNVOTED,
      isActive: true,
    },
  })

  await prisma.agenda.upsert({
    where: { id: 'proj-002' },
    update: {},
    create: {
      id: 'proj-002',
      title: 'Desenvolvimento de Novo Website Corporativo',
      description:
        'Proposta para redesenho completo do site institucional, com foco em melhorar a experiência do usuário, otimização para dispositivos móveis e implementação de recursos de acessibilidade. O projeto inclui contratação de equipe especializada e prazo de entrega em 3 meses.',
      status: AgendaStatus.OPEN,
      category: AgendaCategory.PROJETOS,
      result: AgendaResult.UNVOTED,
      isActive: true,
    },
  })

  // ADMINISTRATIVO
  await prisma.agenda.upsert({
    where: { id: 'adm-001' },
    update: {},
    create: {
      id: 'adm-001',
      title: 'Atualização do Regimento Interno Departamental',
      description:
        'Revisão das normas internas de funcionamento dos departamentos, com proposta de atualizações nas diretrizes para home office, horários flexíveis e novos procedimentos para requisições de materiais e serviços.',
      status: AgendaStatus.OPEN,
      category: AgendaCategory.ADMINISTRATIVO,
      result: AgendaResult.UNVOTED,
      isActive: true,
    },
  })

  await prisma.agenda.upsert({
    where: { id: 'adm-002' },
    update: {},
    create: {
      id: 'adm-002',
      title: 'Reestruturação do Plano de Cargos e Salários',
      description:
        'Proposta de revisão da estrutura organizacional, incluindo a criação de novos cargos, atualização das responsabilidades e competências exigidas, bem como a adequação da tabela salarial às práticas de mercado atual.',
      status: AgendaStatus.OPEN,
      category: AgendaCategory.ADMINISTRATIVO,
      result: AgendaResult.UNVOTED,
      isActive: true,
    },
  })

  // ELEICOES
  await prisma.agenda.upsert({
    where: { id: 'ele-001' },
    update: {},
    create: {
      id: 'ele-001',
      title: 'Eleição para Diretoria Executiva - Biênio 2023/2025',
      description:
        'Votação para eleger os membros da nova diretoria executiva que atuará pelos próximos dois anos. Os candidatos apresentaram seus planos de gestão e compromissos durante a assembleia anterior, conforme documentação anexa.',
      status: AgendaStatus.OPEN,
      category: AgendaCategory.ELEICOES,
      result: AgendaResult.UNVOTED,
      isActive: true,
    },
  })

  await prisma.agenda.upsert({
    where: { id: 'ele-002' },
    update: {},
    create: {
      id: 'ele-002',
      title: 'Eleição do Conselho Fiscal - Mandato 2023',
      description:
        'Deliberação para escolha dos membros titulares e suplentes do Conselho Fiscal para o próximo exercício. Os candidatos apresentaram suas qualificações e experiências relevantes para a função de fiscalização das contas e operações financeiras.',
      status: AgendaStatus.OPEN,
      category: AgendaCategory.ELEICOES,
      result: AgendaResult.UNVOTED,
      isActive: true,
    },
  })

  // ESTATUTARIO
  await prisma.agenda.upsert({
    where: { id: 'est-001' },
    update: {},
    create: {
      id: 'est-001',
      title: 'Alteração do Estatuto Social - Artigos 15 a 20',
      description:
        'Proposta de modificação nos artigos relacionados às competências do Conselho Deliberativo, ampliando o escopo de atuação e reorganizando as responsabilidades para garantir maior agilidade nas tomadas de decisão estratégicas.',
      status: AgendaStatus.OPEN,
      category: AgendaCategory.ESTATUTARIO,
      result: AgendaResult.UNVOTED,
      isActive: true,
    },
  })

  await prisma.agenda.upsert({
    where: { id: 'est-002' },
    update: {},
    create: {
      id: 'est-002',
      title: 'Inclusão de Cláusula sobre Sustentabilidade no Estatuto',
      description:
        'Proposta para incluir no estatuto um compromisso institucional com práticas de sustentabilidade ambiental, social e governança (ESG), estabelecendo diretrizes e metas para todas as atividades da organização.',
      status: AgendaStatus.OPEN,
      category: AgendaCategory.ESTATUTARIO,
      result: AgendaResult.UNVOTED,
      isActive: true,
    },
  })

  // FINANCEIRO
  await prisma.agenda.upsert({
    where: { id: 'fin-001' },
    update: {},
    create: {
      id: 'fin-001',
      title: 'Aprovação do Orçamento Anual - Exercício 2023',
      description:
        'Deliberação sobre a proposta orçamentária para o próximo ano fiscal, detalhando receitas projetadas, plano de investimentos, despesas operacionais e provisões para contingências. Inclui comparativo com o exercício anterior e justificativas para as principais variações.',
      status: AgendaStatus.OPEN,
      category: AgendaCategory.FINANCEIRO,
      result: AgendaResult.UNVOTED,
      isActive: true,
    },
  })

  await prisma.agenda.upsert({
    where: { id: 'fin-002' },
    update: {},
    create: {
      id: 'fin-002',
      title: 'Política de Investimentos e Aplicações Financeiras',
      description:
        'Proposta de nova política para gestão das reservas financeiras da organização, definindo limites de exposição a riscos, categorias de aplicação permitidas, critérios de escolha de instituições financeiras e procedimentos para acompanhamento de desempenho.',
      status: AgendaStatus.OPEN,
      category: AgendaCategory.FINANCEIRO,
      result: AgendaResult.UNVOTED,
      isActive: true,
    },
  })

  // OUTROS
  await prisma.agenda.upsert({
    where: { id: 'out-001' },
    update: {},
    create: {
      id: 'out-001',
      title: 'Programa de Voluntariado Corporativo',
      description:
        'Apresentação de programa estruturado para estimular o voluntariado entre os colaboradores, com estabelecimento de banco de horas específico, parcerias com instituições sociais e métricas de impacto social a serem reportadas anualmente.',
      status: AgendaStatus.OPEN,
      category: AgendaCategory.OUTROS,
      result: AgendaResult.UNVOTED,
      isActive: true,
    },
  })

  await prisma.agenda.upsert({
    where: { id: 'out-002' },
    update: {},
    create: {
      id: 'out-002',
      title: 'Parceria com Universidades para Programa de Estágio',
      description:
        'Proposta de convênio com instituições de ensino superior para implementação de programa regular de estágios, com definição de áreas participantes, processo seletivo, plano de desenvolvimento e possibilidade de efetivação dos melhores talentos.',
      status: AgendaStatus.OPEN,
      category: AgendaCategory.OUTROS,
      result: AgendaResult.UNVOTED,
      isActive: true,
    },
  })

  // Agendas com outros status para testes
  await prisma.agenda.upsert({
    where: { id: 'fin-003' },
    update: {},
    create: {
      id: 'fin-003',
      title: 'Análise de Viabilidade para Aquisição de Imóvel',
      description:
        'Estudo detalhado sobre a oportunidade de compra de imóvel para expansão das atividades operacionais, incluindo análises de retorno sobre investimento, impacto no fluxo de caixa e alternativas de financiamento.',
      status: AgendaStatus.IN_PROGRESS,
      category: AgendaCategory.FINANCEIRO,
      result: AgendaResult.UNVOTED,
      isActive: true,
    },
  })

  await prisma.agenda.upsert({
    where: { id: 'proj-003' },
    update: {},
    create: {
      id: 'proj-003',
      title: 'Implementação de Sistema de Gestão Integrado (ERP)',
      description:
        'Projeto para substituição dos sistemas legados por uma solução ERP completa, visando integrar todos os processos operacionais, financeiros e administrativos em uma única plataforma. Inclui fases de seleção de fornecedor, implementação e treinamento.',
      status: AgendaStatus.FINISHED,
      category: AgendaCategory.PROJETOS,
      result: AgendaResult.APPROVED,
      isActive: true,
      yesVotes: 12,
      noVotes: 3,
      totalVotes: 15,
    },
  })

  await prisma.agenda.upsert({
    where: { id: 'adm-003' },
    update: {},
    create: {
      id: 'adm-003',
      title: 'Política de Segurança da Informação',
      description:
        'Estabelecimento de diretrizes e procedimentos para garantir a proteção de dados sensíveis da organização, incluindo classificação de informações, controles de acesso, procedimentos para resposta a incidentes e programa de conscientização dos colaboradores.',
      status: AgendaStatus.FINISHED,
      category: AgendaCategory.ADMINISTRATIVO,
      result: AgendaResult.REJECTED,
      isActive: true,
      yesVotes: 7,
      noVotes: 10,
      totalVotes: 17,
    },
  })

  console.log('Agendas de teste criadas com sucesso!')
}

main()
  .catch((e) => {
    console.error(e)
    process.exit(1)
  })
  .finally(async () => {
    await prisma.$disconnect()
  })
