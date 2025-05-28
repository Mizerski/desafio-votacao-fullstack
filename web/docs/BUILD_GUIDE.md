# Guia de Build - Web Votação

Este projeto suporta build tanto com **Bun** quanto com **npm/Vite**. Aqui estão as instruções para cada opção:

##  Scripts Disponíveis

### Desenvolvimento
```bash
# Com Vite (padrão)
npm run dev

# Com Bun (experimental)
bun dev:bun
```

### Build para Produção

#### Build com Vite (npm)
```bash
# Build completo com TypeScript + Vite
npm run build

# Preview da build
npm run preview
```

#### Build com Bun
```bash
# Build otimizado com Bun (code splitting)
npm run build:bun
# ou
bun run build:bun

# Build standalone (arquivo único)
npm run build:bun:standalone

# Preview da build do Bun
npm run preview:bun
```

#### Build com Ambos
```bash
# Executa build tanto com Vite quanto com Bun
npm run build:all
```

##  Estrutura de Saída

- **Vite**: `dist/` - Build padrão para produção
- **Bun**: `dist-bun/` - Build alternativo com Bun

##  Comparação de Performance

### Vite
- [x] Amplamente testado e estável
- [x] Hot Module Replacement (HMR) otimizado
- [x] Ecossistema maduro de plugins
- [x] Mais lento em builds grandes

### Bun
- [x] Build extremamente rápido
- [x] Menor consumo de memória
- [x] TypeScript nativo
- [x] Ainda em desenvolvimento ativo
- [x] Alguns plugins podem não funcionar

##  Configurações

### Vite
Configuração em `vite.config.ts`

### Bun
Configuração em `bun.config.ts` (opcional, já está configurado por padrão)

##  Instalação de Dependências

```bash
# Com npm
npm install

# Com Bun (mais rápido)
bun install
```

##  Comandos Úteis

```bash
# Linting
npm run lint

# Type checking
npx tsc --noEmit

# Limpeza de cache
rm -rf node_modules/.vite
rm -rf .bun
```

##  Deploy

Para produção, recomendamos usar a build do Vite (`npm run build`) por sua estabilidade, mas você pode experimentar com a build do Bun para projetos menores ou em ambientes de desenvolvimento.

```bash
# Build para produção (recomendado)
npm run build

# Ou experimentar com Bun
npm run build:bun
``` 

Para mais detalhes, consulte o [TESTING_BUILDS.md](TESTING_BUILDS.md)