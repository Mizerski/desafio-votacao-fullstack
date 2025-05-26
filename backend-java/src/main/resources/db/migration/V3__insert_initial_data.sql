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
VALUES
    -- Senha: acesso12345 (BCrypt hash)
    (
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
        is_active,
        created_at,
        updated_at
    )
VALUES
    -- Agenda 1 - DRAFT
    (
        '660e8400-e29b-41d4-a716-446655440001',
        'Implementação de Sistema de Monitoramento',
        'Proposta para implementação de câmeras e sistema de monitoramento nas áreas comuns.',
        'DRAFT',
        'PROJETOS',
        'UNVOTED',
        0,
        0,
        0,
        true,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),
    -- Agenda 2 - DRAFT
    (
        '660e8400-e29b-41d4-a716-446655440002',
        'Eleição para Representante do Conselho Fiscal',
        'Votação para escolha do novo representante do conselho fiscal para o biênio 2024-2025.',
        'DRAFT',
        'ELEICOES',
        'UNVOTED',
        0,
        0,
        0,
        true,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),
    -- Agenda 3 - FINISHED (Aprovada)
    (
        '660e8400-e29b-41d4-a716-446655440003',
        'Implementação de Coleta Seletiva',
        'Projeto para implementação de sistema de coleta seletiva no condomínio.',
        'FINISHED',
        'OUTROS',
        'APPROVED',
        35,
        30,
        5,
        true,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),
    -- Agenda 4 - FINISHED (Reprovada)
    (
        '660e8400-e29b-41d4-a716-446655440004',
        'Festa de Fim de Ano',
        'Aprovação do orçamento e planejamento da festa de fim de ano.',
        'FINISHED',
        'OUTROS',
        'REJECTED',
        20,
        8,
        12,
        true,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    );