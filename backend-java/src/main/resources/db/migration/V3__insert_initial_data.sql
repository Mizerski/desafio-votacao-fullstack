-- Inserção do usuário inicial
INSERT INTO
    users (
        id,
        name,
        email,
        password,
        document,
        role,
        is_active,
        is_email_verified,
        is_account_non_expired,
        is_account_non_locked,
        is_credentials_non_expired,
        created_at,
        updated_at
    )
VALUES (
        '550e8400-e29b-41d4-a716-446655440000',
        'Usuário Teste',
        'acesso@email.com',
        '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LsY3.71v.NBqWvbkK',
        '12345678900',
        'USER',
        true,
        true,
        true,
        true,
        true,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    );

-- Inserção de agendas iniciais
INSERT INTO
    agendas (
        id,
        title,
        description,
        status,
        category,
        result,
        total_votes,
        yes_votes,
        no_votes,
        is_active
    )
VALUES
    -- Agendas em DRAFT
    (
        '660e8400-e29b-41d4-a716-446655440001',
        'Implementação de Novo Sistema de Gestão',
        'Proposta para implementação de um novo sistema de gestão integrada para melhorar a eficiência operacional.',
        'DRAFT',
        'PROJETOS',
        'UNVOTED',
        0,
        0,
        0,
        true
    ),
    (
        '660e8400-e29b-41d4-a716-446655440002',
        'Alteração no Horário de Funcionamento',
        'Proposta de alteração do horário de funcionamento para melhor atendimento aos associados.',
        'DRAFT',
        'ADMINISTRATIVO',
        'UNVOTED',
        0,
        0,
        0,
        true
    ),
    (
        '660e8400-e29b-41d4-a716-446655440003',
        'Eleição para Representante do Conselho Fiscal',
        'Votação para escolha do novo representante do conselho fiscal para o biênio 2024-2025.',
        'DRAFT',
        'ELEICOES',
        'UNVOTED',
        0,
        0,
        0,
        true
    ),
    (
        '660e8400-e29b-41d4-a716-446655440004',
        'Revisão do Regimento Interno',
        'Atualização do regimento interno com novas regras de convivência.',
        'DRAFT',
        'ESTATUTARIO',
        'UNVOTED',
        0,
        0,
        0,
        true
    ),
    -- Agendas FINISHED
    (
        '660e8400-e29b-41d4-a716-446655440005',
        'Reforma da Área de Lazer',
        'Projeto de revitalização completa da área de lazer, incluindo playground e academia.',
        'FINISHED',
        'PROJETOS',
        'APPROVED',
        15,
        12,
        3,
        true
    ),
    (
        '660e8400-e29b-41d4-a716-446655440006',
        'Regulamento de Uso do Salão de Festas',
        'Atualização das regras de utilização do salão de festas.',
        'FINISHED',
        'ADMINISTRATIVO',
        'REJECTED',
        20,
        8,
        12,
        true
    ),
    (
        '660e8400-e29b-41d4-a716-446655440007',
        'Eleição Extraordinária - Conselho Consultivo',
        'Eleição para preenchimento de vaga no conselho consultivo.',
        'FINISHED',
        'ELEICOES',
        'APPROVED',
        30,
        25,
        5,
        true
    ),
    (
        '660e8400-e29b-41d4-a716-446655440008',
        'Alteração de Multas e Penalidades',
        'Proposta de atualização das multas e penalidades do condomínio.',
        'FINISHED',
        'ESTATUTARIO',
        'TIE',
        40,
        20,
        20,
        true
    );