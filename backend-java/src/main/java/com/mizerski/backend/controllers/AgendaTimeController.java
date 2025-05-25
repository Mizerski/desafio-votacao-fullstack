package com.mizerski.backend.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mizerski.backend.dtos.response.AgendaResponse;
import com.mizerski.backend.models.enums.VoteType;
import com.mizerski.backend.services.AgendaTimeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller responsável por gerenciar operações de tempo das pautas.
 * Não implementa idempotência pois as operações são naturalmente idempotentes
 * ou devem sempre executar (como cálculo de resultados).
 */
@RestController
@RequestMapping("/api/v1/agendas")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Gestão de Tempo de Pautas", description = "Operações para controle de tempo e status das pautas")
public class AgendaTimeController {

    private final AgendaTimeService agendaTimeService;

    /**
     * Inicia o timer de uma pauta
     * 
     * @param agendaId          ID da pauta a ser iniciada
     * @param durationInMinutes Duração da pauta em minutos (opcional, padrão: 60)
     * @return ResponseEntity com dados da pauta iniciada
     */
    @PostMapping("/{agendaId}/start")
    @Operation(summary = "Iniciar pauta", description = "Inicia o timer de uma pauta e altera seu status para IN_PROGRESS")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pauta iniciada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Pauta não pode ser iniciada (cancelada ou encerrada)"),
            @ApiResponse(responseCode = "404", description = "Pauta não encontrada")
    })
    public ResponseEntity<AgendaResponse> startAgendaTimer(
            @PathVariable String agendaId,
            @RequestParam(defaultValue = "60") int durationInMinutes) {

        log.info("Iniciando pauta {} com duração de {} minutos", agendaId, durationInMinutes);

        try {
            AgendaResponse response = agendaTimeService.startAgendaTimer(agendaId, durationInMinutes);
            log.info("Pauta {} iniciada com sucesso", agendaId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Erro ao iniciar pauta {}: {}", agendaId, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Atualiza os contadores de votos de uma pauta
     * 
     * @param agendaId ID da pauta
     * @param voteType Tipo de voto (YES/NO)
     * @return ResponseEntity com dados da pauta atualizada
     */
    @PutMapping("/{agendaId}/votes")
    @Operation(summary = "Atualizar contadores de votos", description = "Atualiza os contadores de votos de uma pauta específica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contadores atualizados com sucesso"),
            @ApiResponse(responseCode = "404", description = "Pauta não encontrada")
    })
    public ResponseEntity<AgendaResponse> updateAgendaVotes(
            @PathVariable String agendaId,
            @RequestParam VoteType voteType) {

        log.debug("Atualizando contadores da pauta {} com voto {}", agendaId, voteType);

        try {
            AgendaResponse response = agendaTimeService.updateAgendaVotes(agendaId, voteType);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Erro ao atualizar contadores da pauta {}: {}", agendaId, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Calcula e finaliza o resultado de uma pauta
     * 
     * @param agendaId ID da pauta a ser finalizada
     * @return ResponseEntity com dados da pauta finalizada
     */
    @PostMapping("/{agendaId}/finalize")
    @Operation(summary = "Finalizar pauta", description = "Calcula o resultado final da pauta e altera seu status para FINISHED")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pauta finalizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Pauta não encontrada")
    })
    public ResponseEntity<AgendaResponse> finalizeAgenda(@PathVariable String agendaId) {
        log.info("Finalizando pauta {}", agendaId);

        try {
            AgendaResponse response = agendaTimeService.calculateAgendaResult(agendaId);
            log.info("Pauta {} finalizada com resultado: {}", agendaId, response.getResult());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Erro ao finalizar pauta {}: {}", agendaId, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}