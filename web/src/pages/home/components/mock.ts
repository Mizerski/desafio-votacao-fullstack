import { Agenda } from '../../../shared/types/agenda'

export const mockAgendas: Agenda[] = [
  {
    id: '1',
    title: 'Aprovação do orçamento anual',
    description:
      'Votação para aprovação do orçamento anual da cooperativa para o próximo exercício financeiro.',
    status: 'em_andamento',
    category: 'Projetos',
    session: {
      endTime: '2025-05-19T10:00:00Z',
      startTime: '2025-05-19T09:00:00Z',
    },
    result: 'Em andamento',
    votes: {
      yes: 0,
      no: 0,
    },
  },
  {
    id: '2',
    title: 'Avaliação do diretor executivo',
    description:
      'Avaliação do diretor executivo da cooperativa para o exercício financeiro atual.',
    status: 'encerrada',
    category: 'Eleições',
    session: {
      endTime: '2025-05-19T10:00:00Z',
      startTime: '2025-05-19T09:00:00Z',
    },
    result: 'Aprovado',
    votes: {
      yes: 10,
      no: 5,
    },
  },
  {
    id: '3',
    title: 'Reunião anual da cooperativa',
    description:
      'Reunião anual da cooperativa para discussão e aprovação de assuntos relevantes.',
    status: 'não_iniciada',
    category: 'Estatutário',
    session: undefined,
    result: 'Empate',
    votes: {
      yes: 10,
      no: 10,
    },
  },
  {
    id: '4',
    title: 'Reunião de diretores',
    description:
      'Reunião de diretores da cooperativa para discussão e aprovação de assuntos relevantes.',
    status: 'cancelada',
    category: 'Administrativo',
    session: undefined,
    result: 'Reprovado',
    votes: {
      yes: 10,
      no: 12,
    },
  },
]
