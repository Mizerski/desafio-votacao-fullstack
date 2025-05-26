# Arquitetura Frontend

## 1. Estrutura de Diretórios

```
src/
├── assets/           # Imagens, ícones e outros assets
├── components/       # Componentes reutilizáveis
│   ├── auth/        # Componentes de autenticação
│   ├── lib/         # Componentes de biblioteca
│   └── ui/          # Componentes de UI
├── lib/             # Utilitários e helpers
├── pages/           # Páginas da aplicação
│   ├── home/        # Página principal
│   │   ├── components/  # Componentes específicos
│   │   ├── contexts/   # Contextos da página
│   │   └── hooks/      # Hooks customizados
│   └── login/      # Página de login
├── shared/         # Código compartilhado
│   ├── hooks/      # Hooks globais
│   └── types/      # Tipos TypeScript
└── App.tsx         # Componente raiz
```

## 2. Padrões de Design

### Componentes

```tsx
// Exemplo de componente com TypeScript
interface ButtonProps {
  variant: 'primary' | 'secondary';
  children: React.ReactNode;
  onClick?: () => void;
}

export const Button: React.FC<ButtonProps> = ({
  variant,
  children,
  onClick
}) => {
  return (
    <button
      className={`btn btn-${variant}`}
      onClick={onClick}
    >
      {children}
    </button>
  );
};
```

### Hooks Customizados

```tsx
// Hook para gerenciar estado de loading
export const useLoading = (initialState = false) => {
  const [isLoading, setIsLoading] = useState(initialState);
  
  const startLoading = () => setIsLoading(true);
  const stopLoading = () => setIsLoading(false);
  
  return { isLoading, startLoading, stopLoading };
};
```

### Contextos

```tsx
// Contexto de autenticação
interface AuthContextData {
  user: User | null;
  signIn: (credentials: SignInCredentials) => Promise<void>;
  signOut: () => void;
}

export const AuthContext = createContext<AuthContextData>({} as AuthContextData);

export const AuthProvider: React.FC = ({ children }) => {
  // Implementação...
};
```

## 3. Gerenciamento de Estado

### Local State
- `useState` para estado simples de componente
- `useReducer` para estado complexo

### Context API
- Usado para estado global
- Separado por domínio (auth, theme, etc)

### React Query
- Cache e sincronização de dados
- Gerenciamento de estado do servidor

## 4. Roteamento

```tsx
// App.tsx
import { BrowserRouter, Routes, Route } from 'react-router-dom';

const App = () => (
  <BrowserRouter>
    <Routes>
      <Route path="/" element={<Home />} />
      <Route path="/login" element={<Login />} />
      <Route path="/agenda/:id" element={<AgendaDetails />} />
    </Routes>
  </BrowserRouter>
);
```

## 5. Estilização

### TailwindCSS
- Utility-first CSS
- Design system consistente
- Componentes reutilizáveis

```tsx
// Exemplo de componente com Tailwind
const Card = ({ children }) => (
  <div className="rounded-lg shadow-md p-4 bg-white">
    {children}
  </div>
);
```

## 6. Formulários

### React Hook Form + Zod
```tsx
const schema = z.object({
  email: z.string().email(),
  password: z.string().min(8)
});

const LoginForm = () => {
  const { register, handleSubmit } = useForm({
    resolver: zodResolver(schema)
  });
  
  return (
    <form onSubmit={handleSubmit(onSubmit)}>
      <input {...register('email')} />
      <input {...register('password')} type="password" />
      <button type="submit">Login</button>
    </form>
  );
};
```

## 7. Comunicação com API

### Axios + React Query
```tsx
// API client
const api = axios.create({
  baseURL: 'http://localhost:8080/api'
});

// Hook para buscar agendas
export const useAgendas = () => {
  return useQuery('agendas', async () => {
    const response = await api.get('/agendas');
    return response.data;
  });
};
```

## 8. Performance

### Code Splitting
```tsx
const AgendaDetails = lazy(() => import('./pages/AgendaDetails'));

// No router
<Route
  path="/agenda/:id"
  element={
    <Suspense fallback={<Loading />}>
      <AgendaDetails />
    </Suspense>
  }
/>
```

### Memoização
```tsx
// Componente memoizado
const ExpensiveComponent = memo(({ data }) => {
  // Renderização custosa
});

// Hook memoizado
const useMemoizedValue = (value: string) => {
  return useMemo(() => {
    // Cálculo custoso
    return value.toUpperCase();
  }, [value]);
};
```

## 9. Testes

### Jest + Testing Library
```tsx
describe('Button', () => {
  it('should call onClick when clicked', () => {
    const onClick = jest.fn();
    render(<Button onClick={onClick}>Click me</Button>);
    
    fireEvent.click(screen.getByText('Click me'));
    expect(onClick).toHaveBeenCalled();
  });
});
```

## 10. Boas Práticas

### Componentização
- Componentes pequenos e focados
- Separação de responsabilidades
- Reutilização de código

### TypeScript
- Tipos fortes
- Interfaces bem definidas
- Documentação inline

### Performance
- Lazy loading
- Memoização quando necessário
- Code splitting por rota

### Acessibilidade
- Semântica HTML
- ARIA labels
- Navegação por teclado 