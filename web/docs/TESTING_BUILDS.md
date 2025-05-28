#  Como Testar os Builds

Depois de fazer o build do seu projeto, você precisa testar se os arquivos estáticos gerados estão funcionando corretamente. Aqui estão as diferentes formas:

##  Comandos Rápidos

### Fazer Build e Testar Tudo
```bash
# Faz build de ambos e mostra onde testar
npm run test:builds

# Depois acesse:
# 📦 Build Vite: http://localhost:4173
# ⚡ Build Bun: http://localhost:4174
```

### Testar Builds Individualmente

#### Build do Vite
```bash
# Opção 1: Preview nativo do Vite
npm run preview

# Opção 2: Servidor customizado
npm run serve:vite
```

#### Build do Bun
```bash
# Servidor para build do Bun
npm run serve:bun
```

##  Métodos Alternativos

### 1. Com Python (se disponível)
```bash
# Para build do Vite
cd dist && python -m http.server 8000

# Para build do Bun
cd dist-bun && python -m http.server 8001
```

### 2. Com Node.js (http-server)
```bash
# Instalar globalmente
npm install -g http-server

# Servir builds
http-server dist -p 8000     # Vite
http-server dist-bun -p 8001 # Bun
```

### 3. Com Bun nativo
```bash
# Para qualquer pasta
bun --port 8000 dist         # Vite
bun --port 8001 dist-bun     # Bun
```

##  Comparando os Results

Depois que os servidores estiverem rodando, você pode:

### Verificar Performance
```bash
# Tamanho dos arquivos
ls -lah dist/assets/
ls -lah dist-bun/

# Lighthouse no navegador (F12 > Lighthouse)
# Teste de velocidade de carregamento
```

### Funcionalidades
- ✅ Navegação entre páginas
- ✅ Formulários funcionando
- ✅ Autenticação
- ✅ API calls
- ✅ Responsividade

##  Troubleshooting

### Build não carrega
```bash
# Verifique se o arquivo existe
ls -la dist/
ls -la dist-bun/

# Reconstrua se necessário
npm run build:all
```

### Erro 404 em rotas
Para aplicações SPA (Single Page Application), você pode precisar configurar redirects:

```bash
# Com serve (já configurado automaticamente)
serve dist -s    # -s = single page app mode

# Com http-server
http-server dist -o --proxy http://localhost:8080?
```

### CSS não carrega
Se o CSS não carregar, verifique:
- Caminhos dos arquivos no `index.html`
- Configuração do `base` no `vite.config.ts`
- Headers CORS do servidor

##  Testando em Dispositivos

### Rede Local
```bash
# Descubra seu IP
ifconfig | grep "inet "

# Serve na rede local
serve dist -l 4173        # Vite
serve dist-bun -l 4174    # Bun

# Acesse de outro dispositivo: http://SEU_IP:PORTA
```

### Ngrok (Túnel Público)
```bash
# Instale ngrok
brew install ngrok  # macOS

# Exponha localmente
ngrok http 4173  # Para build do Vite
ngrok http 4174  # Para build do Bun
```

##  Checklist Final

Antes de fazer deploy, teste:

- [ ] 📦 Build do Vite funcionando
- [ ] ⚡ Build do Bun funcionando  
- [ ] 🔄 Todas as rotas carregam
- [ ] 📱 Responsivo em mobile
- [ ] ⚡ Performance aceitável
- [ ] 🔐 Autenticação funcionando
- [ ] 🌐 APIs conectando
- [ ] 🎨 Estilos carregando
- [ ] 📝 Formulários validando

##  Próximos Passos

Quando estiver tudo funcionando localmente:

1. **Para deploy simples**: Use `dist/` (build do Vite)
2. **Para performance**: Teste `dist-bun/` em staging
3. **Para CI/CD**: Configure pipeline com `npm run test:builds` 