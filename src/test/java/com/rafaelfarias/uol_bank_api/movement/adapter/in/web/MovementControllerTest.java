package com.rafaelfarias.uol_bank_api.movement.adapter.in.web;

import com.rafaelfarias.uol_bank_api.movement.domain.Movement;
import com.rafaelfarias.uol_bank_api.movement.domain.MovementType;
import com.rafaelfarias.uol_bank_api.movement.application.port.in.GetAccountStatementUseCase;
import com.rafaelfarias.uol_bank_api.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MovementController.class)
class MovementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GetAccountStatementUseCase getAccountStatementUseCase;

    @Test
    void deveRetornarExtratoDaConta() throws Exception {
        when(getAccountStatementUseCase.getStatement(1L)).thenReturn(List.of(
                new Movement(10L, MovementType.DEBIT, 2L, new BigDecimal("30.00"), Instant.now())
        ));

        mockMvc.perform(get("/api/accounts/1/movements"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].transferId").value(10))
                .andExpect(jsonPath("$[0].type").value("DEBIT"))
                .andExpect(jsonPath("$[0].counterpartyAccountId").value(2))
                .andExpect(jsonPath("$[0].amount").value(30.00));
    }

    @Test
    void deveRetornar404QuandoContaNaoExiste() throws Exception {
        when(getAccountStatementUseCase.getStatement(99L))
                .thenThrow(new ResourceNotFoundException("Conta 99 nao encontrada"));

        mockMvc.perform(get("/api/accounts/99/movements"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}
