# Guia de Contribuição

## 🌟 Como Contribuir

### 1. Preparando o Ambiente

1. Fork o repositório
2. Clone seu fork:
```bash
git clone https://github.com/seu-usuario/desafio-votacao-fullstack.git
cd desafio-votacao-fullstack
```

3. Adicione o repositório original como upstream:
```bash
git remote add upstream https://github.com/original/desafio-votacao-fullstack.git
```

### 2. Mantendo seu Fork Atualizado

```bash
git checkout main
git fetch upstream
git merge upstream/main
git push origin main
```

### 3. Criando uma Nova Feature

1. Crie uma branch para sua feature:
```bash
git checkout -b feature/nome-da-feature
```

2. Faça suas alterações seguindo as convenções do projeto

3. Teste suas alterações:
```bash
./mvnw test
```

4. Commit suas alterações:
```bash
git add .
git commit -m "feat: Descrição da sua feature"
```

5. Push para seu fork:
```bash
git push origin feature/nome-da-feature
```

6. Abra um Pull Request

## 📝 Convenções de Código

### Estilo de Código

- Use 4 espaços para indentação
- Limite de 120 caracteres por linha
- Siga o estilo de código do Google Java Style Guide

### Nomenclatura

- Classes: PascalCase
- Métodos e variáveis: camelCase
- Constantes: UPPER_SNAKE_CASE
- Pacotes: lowercase

### Documentação

- Todos os métodos públicos devem ter JavaDoc
- Comentários em português
- Exemplos de uso quando necessário

### Commits

Seguimos o padrão Conventional Commits:

```
<tipo>[escopo opcional]: <descrição>

[corpo opcional]

[rodapé(s) opcional(is)]
```

Tipos:
- feat: Nova funcionalidade
- fix: Correção de bug
- docs: Documentação
- style: Formatação
- refactor: Refatoração
- test: Testes
- chore: Tarefas de build/admin

Exemplo:
```
feat(auth): Adiciona autenticação via JWT

- Implementa geração de token
- Adiciona validação de token
- Configura filtro de segurança

Closes #123
```

## 🧪 Testes

### Regras para Testes

1. Todo código novo deve ter testes
2. Manter cobertura mínima de 80%
3. Testes devem ser independentes
4. Use nomes descritivos

### Exemplo de Teste

```java
@ExtendWith(MockitoExtension.class)
class AgendaServiceTest {
    
    @Mock
    private AgendaRepository agendaRepository;
    
    @InjectMocks
    private AgendaService agendaService;
    
    @Test
    @DisplayName("Deve criar agenda com sucesso")
    void shouldCreateAgenda() {
        // given
        CreateAgendaRequest request = new CreateAgendaRequest();
        request.setTitle("Test Agenda");
        
        // when
        Result<AgendaResponse> result = agendaService.createAgenda(request);
        
        // then
        assertTrue(result.isSuccess());
        assertEquals("Test Agenda", result.getValue().get().getTitle());
    }
}
```

## 🚀 Pull Requests

### Template de PR

```markdown
## Descrição
Descreva o que este PR faz.

## Mudanças
- [ ] Feature 1
- [ ] Feature 2
- [ ] Bug fix 1

## Como Testar
1. Passo 1
2. Passo 2
3. Passo 3

## Screenshots (se aplicável)

## Checklist
- [ ] Testes adicionados/atualizados
- [ ] Documentação atualizada
- [ ] Build passa localmente
- [ ] Lint passa
- [ ] Testes passam
```

### Processo de Review

1. O PR deve ser revisado por pelo menos 1 pessoa
2. Todos os comentários devem ser resolvidos
3. CI deve passar
4. Cobertura de testes deve ser mantida/aumentada

## 🐛 Reportando Bugs

### Template de Issue

```markdown
## Descrição do Bug
Descreva o bug de forma clara e concisa.

## Como Reproduzir
1. Passo 1
2. Passo 2
3. Passo 3

## Comportamento Esperado
Descreva o que deveria acontecer.

## Comportamento Atual
Descreva o que está acontecendo.

## Screenshots
Se aplicável, adicione screenshots.

## Ambiente
- OS: [e.g. Windows 10]
- Java: [e.g. 17.0.2]
- Maven: [e.g. 3.8.4]
```

## 📚 Recursos Úteis

- [Java Style Guide](https://google.github.io/styleguide/javaguide.html)
- [Conventional Commits](https://www.conventionalcommits.org/)
- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)

## ❓ Dúvidas?

Se você tiver dúvidas:

1. Verifique a documentação
2. Procure por issues similares
3. Pergunte no canal de desenvolvimento
4. Abra uma issue com a tag "question" 