package com.mizerski.backend.controllers;

import java.util.concurrent.TimeUnit;

import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mizerski.backend.annotations.ValidUUID;
import com.mizerski.backend.dtos.response.AgendaResponse;
import com.mizerski.backend.models.enums.VoteType;
import com.mizerski.backend.services.AgendaTimeService;
import com.mizerski.backend.services.ExceptionMappingService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller responsável por gerenciar operações de tempo das pautas.
 */
@RestController
@RequestMapping("/api/v1/agendas")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Gestão de Tempo de Pautas", description = "Operações para controle de tempo e status das pautas")
public class AgendaTimeController extends BaseController {

    private final AgendaTimeService agendaTimeService;
    private final ExceptionMappingService exceptionMappingService;

    /**
     * Inicia o timer de uma pauta
     * 
     * @param agendaId          ID da pauta a ser iniciada (UUID válido)
     * @param durationInMinutes Duração da pauta em minutos (1-1440, padrão: 60)
     * @return ResponseEntity com dados da pauta iniciada
     */
    @PostMapping("/{agendaId}/start")
    @Operation(summary = "Iniciar pauta", description = "Inicia o timer de uma pauta e altera seu status para IN_PROGRESS")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pauta iniciada com sucesso"),
            @ApiResponse(responseCode = "400", description = "ID inválido ou duração inválida"),
            @ApiResponse(responseCode = "404", description = "Pauta não encontrada"),
            @ApiResponse(responseCode = "422", description = "Pauta não pode ser iniciada (cancelada ou encerrada)")
    })
    public ResponseEntity<AgendaResponse> startAgendaTimer(
            @PathVariable @ValidUUID(message = "ID da pauta deve ser um UUID válido") String agendaId,

            @RequestParam(defaultValue = "60") @Min(value = 1, message = "Duração deve ser pelo menos 1 minuto") @Max(value = 1440, message = "Duração máxima é 1440 minutos (24 horas)") @Parameter(description = "Duração da pauta em minutos (1-1440)") int durationInMinutes) {

        logOperation("startAgendaTimer",
                String.format("agenda=%s, duration=%d", agendaId, durationInMinutes),
                true);

        try {
            AgendaResponse response = agendaTimeService.startAgendaTimer(agendaId, durationInMinutes);

            logOperation("startAgendaTimer", agendaId, true);

            return ResponseEntity.ok()
                    .cacheControl(CacheControl.noCache())
                    .body(response);
        } catch (Exception e) {
            logOperation("startAgendaTimer", agendaId, false);

            var result = exceptionMappingService.<AgendaResponse>mapExceptionToResult(e);
            return errorMappingService.mapErrorToResponse(result);
        }
    }

    /**
     * Atualiza os contadores de votos de uma pauta
     * 
     * @param agendaId ID da pauta (UUID válido)
     * @param voteType Tipo de voto (YES/NO)
     * @return ResponseEntity com dados da pauta atualizada
     */
    @PutMapping("/{agendaId}/votes")
    @Operation(summary = "Atualizar contadores de votos", description = "Atualiza os contadores de votos de uma pauta específica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contadores atualizados com sucesso"),
            @ApiResponse(responseCode = "400", description = "ID inválido ou tipo de voto inválido"),
            @ApiResponse(responseCode = "404", description = "Pauta não encontrada")
    })
    public ResponseEntity<AgendaResponse> updateAgendaVotes(
            @PathVariable @ValidUUID(message = "ID da pauta deve ser um UUID válido") String agendaId,

            @RequestParam @Parameter(description = "Tipo de voto: YES ou NO") VoteType voteType) {

        logOperation("updateAgendaVotes",
                String.format("agenda=%s, voteType=%s", agendaId, voteType),
                true);

        try {
            AgendaResponse response = agendaTimeService.updateAgendaVotes(agendaId, voteType);

            logOperation("updateAgendaVotes", agendaId, true);

            return ResponseEntity.ok()
                    .cacheControl(CacheControl.noCache())
                    .body(response);
        } catch (Exception e) {
            logOperation("updateAgendaVotes", agendaId, false);

            var result = exceptionMappingService.<AgendaResponse>mapExceptionToResult(e);
            return errorMappingService.mapErrorToResponse(result);
        }
    }

    /**
     * Calcula e finaliza o resultado de uma pauta
     * 
     * @param agendaId ID da pauta a ser finalizada (UUID válido)
     * @return ResponseEntity com dados da pauta finalizada
     */
    @PostMapping("/{agendaId}/finalize")
    @Operation(summary = "Finalizar pauta", description = "Calcula o resultado final da pauta e altera seu status para FINISHED")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pauta finalizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "ID inválido"),
            @ApiResponse(responseCode = "404", description = "Pauta não encontrada"),
            @ApiResponse(responseCode = "422", description = "Pauta não pode ser finalizada")
    })
    public ResponseEntity<AgendaResponse> finalizeAgenda(
            @PathVariable @ValidUUID(message = "ID da pauta deve ser um UUID válido") String agendaId) {

        logOperation("finalizeAgenda", agendaId, true);

        try {
            AgendaResponse response = agendaTimeService.calculateAgendaResult(agendaId);

            logOperation("finalizeAgenda",
                    String.format("agenda=%s, result=%s", agendaId, response.getResult()),
                    true);

            return ResponseEntity.ok()
                    .cacheControl(CacheControl.maxAge(1, TimeUnit.HOURS))
                    .body(response);
        } catch (Exception e) {
            logOperation("finalizeAgenda", agendaId, false);

            var result = exceptionMappingService.<AgendaResponse>mapExceptionToResult(e);
            return errorMappingService.mapErrorToResponse(result);
        }
    }
}