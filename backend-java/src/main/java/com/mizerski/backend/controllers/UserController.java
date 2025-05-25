package com.mizerski.backend.controllers;

import java.util.concurrent.TimeUnit;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mizerski.backend.annotations.ValidUUID;
import com.mizerski.backend.dtos.request.CreateUserRequest;
import com.mizerski.backend.dtos.response.PagedResponse;
import com.mizerski.backend.dtos.response.UserResponse;
import com.mizerski.backend.models.domains.Result;
import com.mizerski.backend.services.ErrorMappingService;
import com.mizerski.backend.services.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller responsável por gerenciar operações relacionadas a usuários.
 */
@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Usuários", description = "API para gerenciamento de usuários")
@Validated
@Slf4j
public class UserController extends BaseController {

    private final UserService userService;

    /**
     * Construtor para injeção de dependência via construtor
     * 
     * @param errorMappingService Serviço de mapeamento de erros
     * @param userService         Serviço de usuários
     */
    public UserController(ErrorMappingService errorMappingService, UserService userService) {
        super(errorMappingService);
        this.userService = userService;
    }

    /**
     * Cria um novo usuário
     *
     * @param request dados do usuário a ser criado
     * @return dados do usuário criado
     */
    @PostMapping
    @Operation(summary = "Criar usuário", description = "Cria um novo usuário no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos"),
            @ApiResponse(responseCode = "409", description = "Email já cadastrado")
    })
    public ResponseEntity<?> createUser(@RequestBody @Valid CreateUserRequest request) {
        logOperation("createUser", request.getEmail(), true);

        Result<UserResponse> result = userService.createUser(request);

        logOperation("createUser", request.getEmail(), result.isSuccess());

        return handleCreateOperation(result, UserResponse::getId);
    }

    /**
     * Busca um usuário pelo ID
     *
     * @param id identificador do usuário (UUID válido)
     * @return dados do usuário encontrado
     */
    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuário por ID", description = "Retorna os dados de um usuário específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
            @ApiResponse(responseCode = "400", description = "ID inválido"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<UserResponse> getUserById(
            @PathVariable @ValidUUID(message = "ID deve ser um UUID válido") String id) {

        logQuery("getUserById", id);

        Result<UserResponse> result = userService.getUserById(id);

        return handleGetOperation(result);
    }

    /**
     * Lista todos os usuários com paginação
     *
     * @param page      Número da página (padrão: 0)
     * @param size      Tamanho da página (padrão: 20, máximo: 100)
     * @param sort      Campo para ordenação (padrão: createdAt)
     * @param direction Direção da ordenação (asc/desc, padrão: desc)
     * @return lista paginada de usuários cadastrados
     */
    @GetMapping
    @Operation(summary = "Listar usuários", description = "Retorna uma lista paginada de todos os usuários cadastrados")
    @ApiResponse(responseCode = "200", description = "Lista de usuários retornada com sucesso")
    public ResponseEntity<PagedResponse<UserResponse>> getAllUsers(
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "Página deve ser maior ou igual a 0") int page,

            @RequestParam(defaultValue = "20") @Min(value = 1, message = "Tamanho deve ser maior que 0") @Max(value = 100, message = "Tamanho máximo é 100") int size,

            @RequestParam(defaultValue = "createdAt") @Parameter(description = "Campo para ordenação: id, name, email, createdAt") String sort,

            @RequestParam(defaultValue = "desc") @Pattern(regexp = "^(asc|desc)$", message = "Direção deve ser 'asc' ou 'desc'") String direction) {

        logQuery("getAllUsers", String.format("page=%d, size=%d", page, size));

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.valueOf(direction.toUpperCase()), sort));
        Result<PagedResponse<UserResponse>> result = userService.getAllUsers(pageable);

        if (result.isSuccess()) {
            return ResponseEntity.ok()
                    .cacheControl(CacheControl.maxAge(2, TimeUnit.MINUTES))
                    .body(result.getValue().orElse(null));
        }

        return errorMappingService.mapErrorToResponse(result);
    }

    /**
     * Busca usuários por email (busca parcial)
     *
     * @param email Email ou parte do email para busca
     * @param page  Número da página (padrão: 0)
     * @param size  Tamanho da página (padrão: 20)
     * @return lista paginada de usuários encontrados
     */
    @GetMapping("/search")
    @Operation(summary = "Buscar usuários por email", description = "Busca usuários que contenham o email especificado")
    @ApiResponse(responseCode = "200", description = "Lista de usuários encontrados")
    public ResponseEntity<PagedResponse<UserResponse>> searchUsersByEmail(
            @RequestParam @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$|^[a-zA-Z0-9._%+-]+$", message = "Email deve ter formato válido ou ser uma busca parcial") String email,

            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {

        logQuery("searchUsersByEmail", String.format("email=%s, page=%d, size=%d", email, page, size));

        Pageable pageable = PageRequest.of(page, size);
        Result<PagedResponse<UserResponse>> result = userService.searchUsersByEmail(email, pageable);

        if (result.isSuccess()) {
            return ResponseEntity.ok()
                    .cacheControl(CacheControl.maxAge(1, TimeUnit.MINUTES)) // Cache menor para buscas
                    .body(result.getValue().orElse(null));
        }

        return errorMappingService.mapErrorToResponse(result);
    }
}
