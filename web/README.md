
# Web

### Tecnologias que usei por aqui

Frontend moderno, rápido e direto ao ponto. Essas são as versões que garantem que tudo funcione como esperado:

```json
{
  "node": "v18.20.5",
  "typescript": "~5.7.3",
  "react": "^19.1.0",
  "react-dom": "^19.1.0",
  "react-router-dom": "^7.6.0",
  "react-hook-form": "^7.56.4",
  "vite": "^6.3.5",
  "tailwindcss": "^4.1.7",
  "zod": "^3.24.4"
}
```

> Manter essas versões ajuda a evitar surpresas na build e inconsistência entre ambientes.

---

### Arquitetura e fluxo geral

A estrutura do projeto foi pensada pra ser clara, fácil de manter e com foco na experiência do usuário. A ideia é que qualquer pessoa consiga entender o fluxo em poucos minutos de navegação no código.

#### Estrutura de pastas

* **/components** → Componentes reutilizáveis da interface
* **/pages** → Páginas principais do app (navegação via rota)
* **/shared** → Tipagens, utilitários, helpers globais
* **/lib** → Integrações, serviços e configuração de bibliotecas externas

---

### Principais fluxos da aplicação

#### Autenticação

O fluxo de login e cadastro é bem direto:
Usuário envia os dados → recebe o JWT → token salvo em `localStorage` ou cookie → acesso liberado ao painel.

```tsx
// Exemplo simplificado #possivelmente foi modificado
const handleSubmit = async (data) => {
  const response = await api.post('/auth', data);
  setToken(response.data.token);
  navigate('/dashboard');
}
```

---

#### Dashboard e pautas

Depois de logado, o usuário vê o dashboard com as pautas disponíveis, podendo filtrar por categoria e status. Tudo carregado dinamicamente da API.

---

#### Criação de pautas

Temos um formulário amigável com validação em tempo real (React Hook Form + Zod). O usuário define:

* Título e descrição
* Categoria (ex: Projetos, Administrativo...)

Nada passa sem validação — nem título de um caractere.

---

#### Visualização e votação

Cada pauta tem sua tela de detalhes onde é possível:

* Ver todas as informações
* Acompanhar resultado parcial (se estiver liberado)
* Votar SIM ou NÃO (caso esteja no período de votação)
* Ver histórico de votações anteriores

---

### Formulários com validação de verdade

Todos os formulários usam React Hook Form integrado com Zod. Isso garante validações seguras, legíveis e fáceis de manter.

```tsx
const schema = z.object({
  title: z.string().min(3, "Título muito curto"),
  description: z.string().min(10, "Descreva melhor a pauta"),
  startDate: z.date().min(new Date(), "Data não pode ser no passado"),
});
```

> A ideia aqui é evitar ao máximo a entrada de dados inválidos logo na origem, poupando problemas mais pra frente.

---

### UI/UX na prática

Interface construída com Tailwind CSS, priorizando:

* Design responsivo (desktop e mobile)
* Acessibilidade nativa
* Feedbacks visuais claros (carregamentos, erros, estados vazios)
* Interações suaves e consistentes

---

### Setup e primeiros passos

1. Instale as dependências:

```bash
bun install
```

---

2. Rode a aplicação localmente:

```bash
bun run dev
```

---

3. Acesse no navegador:

```bash
http://localhost:5173
```

---

Tudo pronto? Agora é só explorar, testar os fluxos e começar a colaborar com o projeto. 