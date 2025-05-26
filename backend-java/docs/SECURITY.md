# Segurança

## 1. Autenticação e Autorização

### JWT Configuration
```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        return http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/api/auth/**",
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/swagger-ui.html",
                    "/actuator/**",
                    "/public/**",
                    "/"
                ).permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated())
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }
}
```

### JWT Service
```java
@Service
public class JwtServiceImpl implements JwtService {
    
    @Value("${jwt.secret:404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970}")
    private String secretKey;

    @Value("${jwt.expiration:86400000}") // 24 horas
    private long jwtExpiration;

    @Value("${jwt.refresh-expiration:604800000}") // 7 dias
    private long refreshExpiration;
    
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }
    
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }
}
```

## 2. Password Encoding

### BCrypt Configuration
```java
@Configuration
public class SecurityConfig {
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

### Exemplo de Uso
```java
@Service
public class UserServiceImpl implements UserService {
    
    private final PasswordEncoder passwordEncoder;
    
    @Override
    @Transactional
    @Idempotent(expireAfterSeconds = 300)
    public Result<UserResponse> createUser(CreateUserRequest request) {
        // Criptografa a senha antes de persistir
        String hashedPassword = passwordEncoder.encode(request.getPassword());
        // Salva usuário com senha criptografada
    }
}
```

## 3. CORS Configuration

```java
@Configuration
public class SecurityConfig {
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true);
        configuration.addAllowedOrigin("http://localhost:3000");
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}
```

## 4. Tratamento de Exceções JWT

```java
@Service
@Slf4j
public class JwtExceptionHandlerService {
    
    private final Map<String, BiConsumer<String, String>> exceptionHandlers;
    
    public JwtExceptionHandlerService() {
        this.exceptionHandlers = new HashMap<>();
        initializeExceptionHandlers();
    }
    
    private void initializeExceptionHandlers() {
        exceptionHandlers.put("ExpiredJwtException", this::handleExpiredToken);
        exceptionHandlers.put("MalformedJwtException", this::handleMalformedToken);
        exceptionHandlers.put("UnsupportedJwtException", this::handleUnsupportedToken);
        exceptionHandlers.put("SecurityException", this::handleInvalidSignature);
        exceptionHandlers.put("JwtException", this::handleInvalidSignature);
        exceptionHandlers.put("IllegalArgumentException", this::handleInvalidArgument);
    }
}
```

## 5. Checklist de Segurança

### ✅ Implementado
- [x] Autenticação JWT com refresh token
- [x] Senhas criptografadas com BCrypt
- [x] CORS configurado
- [x] Tratamento centralizado de exceções JWT
- [x] Proteção contra CSRF (desabilitado por ser API stateless)
- [x] Endpoints públicos e privados configurados
- [x] Roles e permissões (USER, ADMIN)
- [x] Documentação Swagger/OpenAPI com suporte JWT

