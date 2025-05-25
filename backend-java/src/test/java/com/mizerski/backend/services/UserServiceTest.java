package com.mizerski.backend.services;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.mizerski.backend.dtos.request.CreateUserRequest;
import com.mizerski.backend.dtos.response.PagedResponse;
import com.mizerski.backend.dtos.response.UserResponse;
import com.mizerski.backend.models.domains.Result;
import com.mizerski.backend.models.domains.Users;
import com.mizerski.backend.models.entities.UserEntity;
import com.mizerski.backend.models.mappers.UserMapper;
import com.mizerski.backend.repositories.UserRepository;

/**
 * Testes unitários para o serviço de usuários
 * 
 * Testa todos os métodos do UserService incluindo cenários de sucesso e erro
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserService - Testes Unitários")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private ExceptionMappingService exceptionMappingService;

    @InjectMocks
    private UserService userService;

    private CreateUserRequest createUserRequest;
    private UserEntity userEntity;
    private UserResponse userResponse;
    private Users userDomainValid;
    private Users userDomainInvalid;

    /**
     * Configuração inicial dos dados de teste
     */
    @BeforeEach
    void setUp() {
        // Dados de request
        createUserRequest = new CreateUserRequest();
        createUserRequest.setName("João Silva");
        createUserRequest.setEmail("joao@email.com");
        createUserRequest.setPassword("senha123456");
        createUserRequest.setDocument("12345678901");

        // Entidade de usuário
        userEntity = new UserEntity();
        userEntity.setId("user-123");
        userEntity.setName("João Silva");
        userEntity.setEmail("joao@email.com");
        userEntity.setPassword("senha123456");
        userEntity.setDocument("12345678901");
        userEntity.setCreatedAt(LocalDateTime.now());
        userEntity.setUpdatedAt(LocalDateTime.now());

        // Response de usuário
        userResponse = UserResponse.builder()
                .id("user-123")
                .name("João Silva")
                .email("joao@email.com")
                .document("12345678901")
                .createdAt(LocalDateTime.now())
                .totalVotes(0)
                .build();

        // Domínio de usuário válido
        userDomainValid = Users.builder()
                .id("user-123")
                .name("João Silva")
                .email("joao@email.com")
                .password("senha123456")
                .document("12345678901")
                .build();

        // Domínio de usuário inválido
        userDomainInvalid = Users.builder()
                .id("user-123")
                .name("João Silva")
                .email("email-invalido")
                .password("senha123456")
                .document("12345678901")
                .build();
    }

    @Nested
    @DisplayName("Testes do método createUser")
    class CreateUserTests {

        @Test
        @DisplayName("Deve criar usuário com sucesso quando dados válidos")
        void devecriarUsuarioComSucessoQuandoDadosValidos() {
            // Arrange
            when(userRepository.findByEmail(createUserRequest.getEmail())).thenReturn(Optional.empty());
            when(userMapper.fromCreateRequest(createUserRequest)).thenReturn(userDomainValid);
            when(userMapper.toEntity(userDomainValid)).thenReturn(userEntity);
            when(userRepository.save(userEntity)).thenReturn(userEntity);
            when(userMapper.toResponse(userEntity)).thenReturn(userResponse);

            // Act
            Result<UserResponse> result = userService.createUser(createUserRequest);

            // Assert
            assertTrue(result.isSuccess());
            assertTrue(result.getValue().isPresent());
            assertEquals(userResponse, result.getValue().get());
            assertEquals("João Silva", result.getValue().get().getName());
            assertEquals("joao@email.com", result.getValue().get().getEmail());

            // Verificações de interação
            verify(userRepository).findByEmail(createUserRequest.getEmail());
            verify(userMapper).fromCreateRequest(createUserRequest);
            verify(userMapper).toEntity(userDomainValid);
            verify(userRepository).save(userEntity);
            verify(userMapper).toResponse(userEntity);
        }

        @Test
        @DisplayName("Deve retornar erro quando email já existe")
        void deveRetornarErroQuandoEmailJaExiste() {
            // Arrange
            when(userRepository.findByEmail(createUserRequest.getEmail())).thenReturn(Optional.of(userEntity));

            // Act
            Result<UserResponse> result = userService.createUser(createUserRequest);

            // Assert
            assertTrue(result.isError());
            assertTrue(result.getErrorCode().isPresent());
            assertEquals("DUPLICATE_EMAIL", result.getErrorCode().get());
            assertTrue(result.getErrorMessage().get().contains("Email já cadastrado"));

            // Verificações de interação
            verify(userRepository).findByEmail(createUserRequest.getEmail());
            verify(userMapper, never()).fromCreateRequest(any());
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve retornar erro quando email é inválido")
        void deveRetornarErroQuandoEmailInvalido() {
            // Arrange
            when(userRepository.findByEmail(createUserRequest.getEmail())).thenReturn(Optional.empty());
            when(userMapper.fromCreateRequest(createUserRequest)).thenReturn(userDomainInvalid);

            // Act
            Result<UserResponse> result = userService.createUser(createUserRequest);

            // Assert
            assertTrue(result.isError());
            assertTrue(result.getErrorCode().isPresent());
            assertEquals("INVALID_USER", result.getErrorCode().get());
            assertEquals("Email inválido", result.getErrorMessage().get());

            // Verificações de interação
            verify(userRepository).findByEmail(createUserRequest.getEmail());
            verify(userMapper).fromCreateRequest(createUserRequest);
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve retornar erro quando ocorre exceção durante criação")
        void deveRetornarErroQuandoOcorreExcecaoDuranteCriacao() {
            // Arrange
            RuntimeException exception = new RuntimeException("Erro de banco de dados");
            when(userRepository.findByEmail(createUserRequest.getEmail())).thenReturn(Optional.empty());
            when(userMapper.fromCreateRequest(createUserRequest)).thenReturn(userDomainValid);
            when(userMapper.toEntity(userDomainValid)).thenReturn(userEntity);
            when(userRepository.save(userEntity)).thenThrow(exception);
            when(exceptionMappingService.mapExceptionToResult(exception))
                    .thenReturn(Result.error("DATABASE_ERROR", "Erro interno do servidor"));

            // Act
            Result<UserResponse> result = userService.createUser(createUserRequest);

            // Assert
            assertTrue(result.isError());
            assertEquals("DATABASE_ERROR", result.getErrorCode().get());
            assertEquals("Erro interno do servidor", result.getErrorMessage().get());

            // Verificações de interação
            verify(exceptionMappingService).mapExceptionToResult(exception);
        }
    }

    @Nested
    @DisplayName("Testes do método getUserById")
    class GetUserByIdTests {

        @Test
        @DisplayName("Deve buscar usuário por ID com sucesso quando usuário existe")
        void deveBuscarUsuarioPorIdComSucessoQuandoUsuarioExiste() {
            // Arrange
            String userId = "user-123";
            when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
            when(userMapper.toResponse(userEntity)).thenReturn(userResponse);

            // Act
            Result<UserResponse> result = userService.getUserById(userId);

            // Assert
            assertTrue(result.isSuccess());
            assertTrue(result.getValue().isPresent());
            assertEquals(userResponse, result.getValue().get());
            assertEquals(userId, result.getValue().get().getId());

            // Verificações de interação
            verify(userRepository).findById(userId);
            verify(userMapper).toResponse(userEntity);
        }

        @Test
        @DisplayName("Deve retornar erro quando usuário não existe")
        void deveRetornarErroQuandoUsuarioNaoExiste() {
            // Arrange
            String userId = "user-inexistente";
            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            // Act
            Result<UserResponse> result = userService.getUserById(userId);

            // Assert
            assertTrue(result.isError());
            assertEquals("USER_NOT_FOUND", result.getErrorCode().get());
            assertTrue(result.getErrorMessage().get().contains("Usuário não encontrado com ID"));

            // Verificações de interação
            verify(userRepository).findById(userId);
            verify(userMapper, never()).toResponse((UserEntity) any());
        }

        @Test
        @DisplayName("Deve retornar erro quando ocorre exceção durante busca")
        void deveRetornarErroQuandoOcorreExcecaoDuranteBusca() {
            // Arrange
            String userId = "user-123";
            RuntimeException exception = new RuntimeException("Erro de conexão");
            when(userRepository.findById(userId)).thenThrow(exception);
            when(exceptionMappingService.mapExceptionToResult(exception))
                    .thenReturn(Result.error("DATABASE_ERROR", "Erro interno do servidor"));

            // Act
            Result<UserResponse> result = userService.getUserById(userId);

            // Assert
            assertTrue(result.isError());
            assertEquals("DATABASE_ERROR", result.getErrorCode().get());

            // Verificações de interação
            verify(userRepository).findById(userId);
            verify(exceptionMappingService).mapExceptionToResult(exception);
        }
    }

    @Nested
    @DisplayName("Testes do método getAllUsers")
    class GetAllUsersTests {

        @Test
        @DisplayName("Deve buscar todos os usuários com paginação com sucesso")
        void deveBuscarTodosUsuariosComPaginacaoComSucesso() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            List<UserEntity> userEntities = Arrays.asList(userEntity);
            Page<UserEntity> page = new PageImpl<>(userEntities, pageable, 1);

            when(userRepository.findAll(pageable)).thenReturn(page);
            when(userMapper.toResponse(userEntity)).thenReturn(userResponse);

            // Act
            Result<PagedResponse<UserResponse>> result = userService.getAllUsers(pageable);

            // Assert
            assertTrue(result.isSuccess());
            assertTrue(result.getValue().isPresent());

            PagedResponse<UserResponse> pagedResponse = result.getValue().get();
            assertEquals(1, pagedResponse.getContent().size());
            assertEquals(0, pagedResponse.getPage());
            assertEquals(10, pagedResponse.getSize());
            assertEquals(1, pagedResponse.getTotalElements());
            assertEquals(userResponse, pagedResponse.getContent().get(0));

            // Verificações de interação
            verify(userRepository).findAll(pageable);
            verify(userMapper).toResponse(userEntity);
        }

        @Test
        @DisplayName("Deve retornar página vazia quando não há usuários")
        void deveRetornarPaginaVaziaQuandoNaoHaUsuarios() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            Page<UserEntity> emptyPage = new PageImpl<>(Arrays.asList(), pageable, 0);

            when(userRepository.findAll(pageable)).thenReturn(emptyPage);

            // Act
            Result<PagedResponse<UserResponse>> result = userService.getAllUsers(pageable);

            // Assert
            assertTrue(result.isSuccess());
            assertTrue(result.getValue().isPresent());

            PagedResponse<UserResponse> pagedResponse = result.getValue().get();
            assertEquals(0, pagedResponse.getContent().size());
            assertEquals(0, pagedResponse.getTotalElements());

            // Verificações de interação
            verify(userRepository).findAll(pageable);
            verify(userMapper, never()).toResponse((UserEntity) any());
        }

        @Test
        @DisplayName("Deve retornar erro quando ocorre exceção durante busca paginada")
        void deveRetornarErroQuandoOcorreExcecaoDuranteBuscaPaginada() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            RuntimeException exception = new RuntimeException("Erro de paginação");
            when(userRepository.findAll(pageable)).thenThrow(exception);
            when(exceptionMappingService.mapExceptionToResult(exception))
                    .thenReturn(Result.error("PAGINATION_ERROR", "Erro na paginação"));

            // Act
            Result<PagedResponse<UserResponse>> result = userService.getAllUsers(pageable);

            // Assert
            assertTrue(result.isError());
            assertEquals("PAGINATION_ERROR", result.getErrorCode().get());

            // Verificações de interação
            verify(userRepository).findAll(pageable);
            verify(exceptionMappingService).mapExceptionToResult(exception);
        }
    }

    @Nested
    @DisplayName("Testes do método searchUsersByEmail")
    class SearchUsersByEmailTests {

        @Test
        @DisplayName("Deve buscar usuários por email com sucesso quando encontra resultados")
        void deveBuscarUsuariosPorEmailComSucessoQuandoEncontraResultados() {
            // Arrange
            String email = "joao";
            Pageable pageable = PageRequest.of(0, 10);
            List<UserEntity> userEntities = Arrays.asList(userEntity);
            Page<UserEntity> page = new PageImpl<>(userEntities, pageable, 1);

            when(userRepository.findByEmailContainingIgnoreCase(email, pageable)).thenReturn(page);
            when(userMapper.toResponse(userEntity)).thenReturn(userResponse);

            // Act
            Result<PagedResponse<UserResponse>> result = userService.searchUsersByEmail(email, pageable);

            // Assert
            assertTrue(result.isSuccess());
            assertTrue(result.getValue().isPresent());

            PagedResponse<UserResponse> pagedResponse = result.getValue().get();
            assertEquals(1, pagedResponse.getContent().size());
            assertEquals(userResponse, pagedResponse.getContent().get(0));

            // Verificações de interação
            verify(userRepository).findByEmailContainingIgnoreCase(email, pageable);
            verify(userMapper).toResponse(userEntity);
        }

        @Test
        @DisplayName("Deve retornar página vazia quando não encontra usuários por email")
        void deveRetornarPaginaVaziaQuandoNaoEncontraUsuariosPorEmail() {
            // Arrange
            String email = "inexistente";
            Pageable pageable = PageRequest.of(0, 10);
            Page<UserEntity> emptyPage = new PageImpl<>(Arrays.asList(), pageable, 0);

            when(userRepository.findByEmailContainingIgnoreCase(email, pageable)).thenReturn(emptyPage);

            // Act
            Result<PagedResponse<UserResponse>> result = userService.searchUsersByEmail(email, pageable);

            // Assert
            assertTrue(result.isSuccess());
            assertTrue(result.getValue().isPresent());

            PagedResponse<UserResponse> pagedResponse = result.getValue().get();
            assertEquals(0, pagedResponse.getContent().size());
            assertEquals(0, pagedResponse.getTotalElements());

            // Verificações de interação
            verify(userRepository).findByEmailContainingIgnoreCase(email, pageable);
            verify(userMapper, never()).toResponse((UserEntity) any());
        }

        @Test
        @DisplayName("Deve buscar usuários com email parcial ignorando case")
        void deveBuscarUsuariosComEmailParcialIgnorandoCase() {
            // Arrange
            String email = "JOAO";
            Pageable pageable = PageRequest.of(0, 10);
            List<UserEntity> userEntities = Arrays.asList(userEntity);
            Page<UserEntity> page = new PageImpl<>(userEntities, pageable, 1);

            when(userRepository.findByEmailContainingIgnoreCase(email, pageable)).thenReturn(page);
            when(userMapper.toResponse(userEntity)).thenReturn(userResponse);

            // Act
            Result<PagedResponse<UserResponse>> result = userService.searchUsersByEmail(email, pageable);

            // Assert
            assertTrue(result.isSuccess());
            assertTrue(result.getValue().isPresent());

            PagedResponse<UserResponse> pagedResponse = result.getValue().get();
            assertEquals(1, pagedResponse.getContent().size());

            // Verificações de interação
            verify(userRepository).findByEmailContainingIgnoreCase(email, pageable);
        }

        @Test
        @DisplayName("Deve retornar erro quando ocorre exceção durante busca por email")
        void deveRetornarErroQuandoOcorreExcecaoDuranteBuscaPorEmail() {
            // Arrange
            String email = "joao";
            Pageable pageable = PageRequest.of(0, 10);
            RuntimeException exception = new RuntimeException("Erro de busca");
            when(userRepository.findByEmailContainingIgnoreCase(email, pageable)).thenThrow(exception);
            when(exceptionMappingService.mapExceptionToResult(exception))
                    .thenReturn(Result.error("SEARCH_ERROR", "Erro na busca por email"));

            // Act
            Result<PagedResponse<UserResponse>> result = userService.searchUsersByEmail(email, pageable);

            // Assert
            assertTrue(result.isError());
            assertEquals("SEARCH_ERROR", result.getErrorCode().get());

            // Verificações de interação
            verify(userRepository).findByEmailContainingIgnoreCase(email, pageable);
            verify(exceptionMappingService).mapExceptionToResult(exception);
        }
    }

    @Nested
    @DisplayName("Testes de integração e cenários especiais")
    class IntegrationAndSpecialScenariosTests {

        @Test
        @DisplayName("Deve lidar com valores nulos nos parâmetros de entrada")
        void deveLidarComValoresNulosNosParametrosDeEntrada() {
            // Arrange
            when(exceptionMappingService.mapExceptionToResult(any()))
                    .thenReturn(Result.error("NULL_PARAMETER", "Parâmetro nulo"));

            // Test getUserById with null
            Result<UserResponse> result1 = userService.getUserById(null);
            assertTrue(result1.isError());

            // Test searchUsersByEmail with null email
            Pageable pageable = PageRequest.of(0, 10);
            when(userRepository.findByEmailContainingIgnoreCase(isNull(), eq(pageable)))
                    .thenThrow(new IllegalArgumentException("Email não pode ser nulo"));

            Result<PagedResponse<UserResponse>> result2 = userService.searchUsersByEmail(null, pageable);
            assertTrue(result2.isError());
        }

        @Test
        @DisplayName("Deve manter transações corretas nos métodos")
        void deveManterTransacoesCorretasNosMetodos() {
            // Este teste verifica se as anotações @Transactional estão sendo respeitadas
            // Em um ambiente real, isso seria testado com um banco de dados real

            // Arrange
            when(userRepository.findByEmail(createUserRequest.getEmail())).thenReturn(Optional.empty());
            when(userMapper.fromCreateRequest(createUserRequest)).thenReturn(userDomainValid);
            when(userMapper.toEntity(userDomainValid)).thenReturn(userEntity);
            when(userRepository.save(userEntity)).thenReturn(userEntity);
            when(userMapper.toResponse(userEntity)).thenReturn(userResponse);

            // Act
            Result<UserResponse> result = userService.createUser(createUserRequest);

            // Assert
            assertTrue(result.isSuccess());
            // Em um teste de integração real, verificaríamos se a transação foi commitada
        }
    }
}