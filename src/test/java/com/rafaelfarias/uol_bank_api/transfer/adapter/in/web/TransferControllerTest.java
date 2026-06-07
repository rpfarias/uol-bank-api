package com.rafaelfarias.uol_bank_api.transfer.adapter.in.web;

import com.rafaelfarias.uol_bank_api.shared.exception.BusinessRuleException;
import com.rafaelfarias.uol_bank_api.transfer.application.port.in.TransferFundsUseCase;
import com.rafaelfarias.uol_bank_api.transfer.domain.Transfer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransferController.class)
class TransferControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransferFundsUseCase transferFundsUseCase;

    @Test
    void deveRealizarTransferenciaERetornar201() throws Exception {
        when(transferFundsUseCase.transfer(eq(1L), eq(2L), any(BigDecimal.class)))
                .thenReturn(Transfer.restore(10L, 1L, 2L, new BigDecimal("150.00"), java.time.Instant.now()));

        String body = "{\"sourceAccountId\":1,\"targetAccountId\":2,\"amount\":150.00}";

        mockMvc.perform(post("/api/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.sourceAccountId").value(1))
                .andExpect(jsonPath("$.targetAccountId").value(2))
                .andExpect(jsonPath("$.amount").value(150.00));
    }

    @Test
    void deveRetornar400QuandoValorAusente() throws Exception {
        String body = "{\"sourceAccountId\":1,\"targetAccountId\":2}";

        mockMvc.perform(post("/api/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void deveRetornar422QuandoSaldoInsuficiente() throws Exception {
        when(transferFundsUseCase.transfer(any(), any(), any()))
                .thenThrow(new BusinessRuleException("Saldo insuficiente"));

        String body = "{\"sourceAccountId\":1,\"targetAccountId\":2,\"amount\":999999.00}";

        mockMvc.perform(post("/api/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.message").value("Saldo insuficiente"));
    }
}
