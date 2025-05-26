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
import com.mizerski.backend.models.domains.Result;
import com.mizerski.backend.models.enums.VoteType;
import com.mizerski.backend.services.AgendaTimeService;
import com.mizerski.backend.services.ErrorMappingService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller responsável por gerenciar operações de tempo das pautas.
 */
@RestController
@RequestMapping("/api/v1/agendas")
@Validated
@Slf4j
@Tag(name = "Gestão de Tempo de Pautas", description = "Operações para controle de tempo e status das pautas")
public class AgendaTimeController extends BaseController {

        private final AgendaTimeService agendaTimeService;

        public AgendaTimeController(AgendaTimeService agendaTimeService, ErrorMappingService errorMappingService) {
                super(errorMappingService);
                this.agendaTimeService = agendaTimeService;
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

                Result<AgendaResponse> result = agendaTimeService.updateAgendaVotes(agendaId, voteType);

                logOperation("updateAgendaVotes", agendaId, result.isSuccess());

                if (result.isSuccess()) {
                        return ResponseEntity.ok()
                                        .cacheControl(CacheControl.noCache())
                                        .body(result.getValue().orElse(null));
                }

                return errorMappingService.mapErrorToResponse(result);
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

                Result<AgendaResponse> result = agendaTimeService.calculateAgendaResult(agendaId);

                if (result.isSuccess()) {
                        AgendaResponse response = result.getValue().orElse(null);
                        logOperation("finalizeAgenda",
                                        String.format("agenda=%s, result=%s", agendaId,
                                                        response != null ? response.getResult() : "null"),
                                        true);

                        return ResponseEntity.ok()
                                        .cacheControl(CacheControl.maxAge(1, TimeUnit.HOURS))
                                        .body(response);
                }

                logOperation("finalizeAgenda", agendaId, false);
                return errorMappingService.mapErrorToResponse(result);
        }
}