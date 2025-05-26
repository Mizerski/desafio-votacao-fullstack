# Gerenciamento de Estado

## 1. Context API

### AuthContext
```tsx
interface User {
  id: string;
  name: string;
  email: string;
  role: string;
}

interface AuthContextData {
  user: User | null;
  signIn: (credentials: SignInCredentials) => Promise<void>;
  signOut: () => void;
  isAuthenticated: boolean;
}

export const AuthContext = createContext<AuthContextData>({} as AuthContextData);

export const AuthProvider: React.FC = ({ children }) => {
  const [user, setUser] = useState<User | null>(null);

  const signIn = async (credentials: SignInCredentials) => {
    const response = await api.post('/auth/login', credentials);
    const { token, user } = response.data;
    
    setUser(user);
    api.defaults.headers.authorization = `Bearer ${token}`;
    localStorage.setItem('@App:token', token);
    localStorage.setItem('@App:user', JSON.stringify(user));
  };

  const signOut = () => {
    setUser(null);
    localStorage.removeItem('@App:token');
    localStorage.removeItem('@App:user');
    delete api.defaults.headers.authorization;
  };

  useEffect(() => {
    const token = localStorage.getItem('@App:token');
    const user = localStorage.getItem('@App:user');

    if (token && user) {
      api.defaults.headers.authorization = `Bearer ${token}`;
      setUser(JSON.parse(user));
    }
  }, []);

  return (
    <AuthContext.Provider value={{
      user,
      signIn,
      signOut,
      isAuthenticated: !!user
    }}>
      {children}
    </AuthContext.Provider>
  );
};
```

### AgendaContext
```tsx
interface Agenda {
  id: string;
  title: string;
  description: string;
  status: AgendaStatus;
  votes: Vote[];
}

interface AgendaContextData {
  agendas: Agenda[];
  selectedAgenda: Agenda | null;
  isLoading: boolean;
  error: string | null;
  fetchAgendas: () => Promise<void>;
  selectAgenda: (id: string) => void;
  createAgenda: (data: CreateAgendaData) => Promise<void>;
  startVotingSession: (id: string) => Promise<void>;
  vote: (id: string, voteType: VoteType) => Promise<void>;
}

export const AgendaContext = createContext<AgendaContextData>({} as AgendaContextData);

export const AgendaProvider: React.FC = ({ children }) => {
  const [agendas, setAgendas] = useState<Agenda[]>([]);
  const [selectedAgenda, setSelectedAgenda] = useState<Agenda | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const fetchAgendas = async () => {
    try {
      setIsLoading(true);
      const response = await api.get('/agendas');
      setAgendas(response.data);
      setError(null);
    } catch (err) {
      setError('Erro ao carregar agendas');
    } finally {
      setIsLoading(false);
    }
  };

  // ... outros métodos

  return (
    <AgendaContext.Provider value={{
      agendas,
      selectedAgenda,
      isLoading,
      error,
      fetchAgendas,
      selectAgenda,
      createAgenda,
      startVotingSession,
      vote
    }}>
      {children}
    </AgendaContext.Provider>
  );
};
```

## 2. React Query

### Configuração
```tsx
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      refetchOnWindowFocus: false,
      retry: 1,
      staleTime: 5 * 60 * 1000 // 5 minutos
    }
  }
});

export const AppProvider: React.FC = ({ children }) => {
  return (
    <QueryClientProvider client={queryClient}>
      <AuthProvider>
        <AgendaProvider>
          {children}
        </AgendaProvider>
      </AuthProvider>
    </QueryClientProvider>
  );
};
```

### Hooks de Query
```tsx
// Hooks para agendas
export const useAgendas = () => {
  return useQuery(['agendas'], async () => {
    const response = await api.get('/agendas');
    return response.data;
  });
};

export const useAgenda = (id: string) => {
  return useQuery(['agenda', id], async () => {
    const response = await api.get(`/agendas/${id}`);
    return response.data;
  });
};

// Hooks para votos
export const useVotes = (agendaId: string) => {
  return useQuery(['votes', agendaId], async () => {
    const response = await api.get(`/agendas/${agendaId}/votes`);
    return response.data;
  });
};
```

### Hooks de Mutation
```tsx
// Hook para criar agenda
export const useCreateAgenda = () => {
  const queryClient = useQueryClient();
  
  return useMutation(
    async (data: CreateAgendaData) => {
      const response = await api.post('/agendas', data);
      return response.data;
    },
    {
      onSuccess: () => {
        queryClient.invalidateQueries(['agendas']);
      }
    }
  );
};

// Hook para votar
export const useVote = () => {
  const queryClient = useQueryClient();
  
  return useMutation(
    async ({ agendaId, voteType }: VoteData) => {
      const response = await api.post(`/agendas/${agendaId}/votes`, {
        type: voteType
      });
      return response.data;
    },
    {
      onSuccess: (_, { agendaId }) => {
        queryClient.invalidateQueries(['agenda', agendaId]);
        queryClient.invalidateQueries(['votes', agendaId]);
      }
    }
  );
};
```

## 3. Local State com Hooks

### useLoading
```tsx
export const useLoading = (initialState = false) => {
  const [isLoading, setIsLoading] = useState(initialState);
  
  const startLoading = () => setIsLoading(true);
  const stopLoading = () => setIsLoading(false);
  
  return { isLoading, startLoading, stopLoading };
};
```

### useError
```tsx
export const useError = () => {
  const [error, setError] = useState<string | null>(null);
  
  const showError = (message: string) => setError(message);
  const clearError = () => setError(null);
  
  return { error, showError, clearError };
};
```

### useModal
```tsx
export const useModal = (initialState = false) => {
  const [isOpen, setIsOpen] = useState(initialState);
  
  const openModal = () => setIsOpen(true);
  const closeModal = () => setIsOpen(false);
  const toggleModal = () => setIsOpen(prev => !prev);
  
  return { isOpen, openModal, closeModal, toggleModal };
};
```

## 4. Form State com React Hook Form

### useLoginForm
```tsx
interface LoginFormData {
  email: string;
  password: string;
}

const loginSchema = z.object({
  email: z.string().email('Email inválido'),
  password: z.string().min(8, 'Senha deve ter no mínimo 8 caracteres')
});

export const useLoginForm = () => {
  const { signIn } = useAuth();
  const navigate = useNavigate();
  
  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting }
  } = useForm<LoginFormData>({
    resolver: zodResolver(loginSchema)
  });
  
  const onSubmit = handleSubmit(async (data) => {
    try {
      await signIn(data);
      navigate('/');
    } catch (err) {
      toast.error('Erro ao fazer login');
    }
  });
  
  return {
    register,
    onSubmit,
    errors,
    isSubmitting
  };
};
```

### useAgendaForm
```tsx
interface AgendaFormData {
  title: string;
  description: string;
  category: string;
}

const agendaSchema = z.object({
  title: z.string().min(3, 'Título deve ter no mínimo 3 caracteres'),
  description: z.string().min(10, 'Descrição deve ter no mínimo 10 caracteres'),
  category: z.string().min(1, 'Categoria é obrigatória')
});

export const useAgendaForm = () => {
  const { createAgenda } = useCreateAgenda();
  const navigate = useNavigate();
  
  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting }
  } = useForm<AgendaFormData>({
    resolver: zodResolver(agendaSchema)
  });
  
  const onSubmit = handleSubmit(async (data) => {
    try {
      await createAgenda.mutateAsync(data);
      toast.success('Agenda criada com sucesso');
      navigate('/agendas');
    } catch (err) {
      toast.error('Erro ao criar agenda');
    }
  });
  
  return {
    register,
    onSubmit,
    errors,
    isSubmitting
  };
};
``` 