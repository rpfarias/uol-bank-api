package com.rafaelfarias.uol_bank_api.account.adapter.in.web;

import com.rafaelfarias.uol_bank_api.account.application.port.in.CreateAccountUseCase;
import com.rafaelfarias.uol_bank_api.account.application.port.in.GetAccountUseCase;
import com.rafaelfarias.uol_bank_api.account.domain.Account;
import com.rafaelfarias.uol_bank_api.shared.exception.ResourceNotFoundException;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateAccountUseCase createAccountUseCase;

    @MockitoBean
    private GetAccountUseCase getAccountUseCase;

    @Test
    void deveCriarContaERetornar201() throws Exception {
        when(createAccountUseCase.create(eq("Alice"), any(BigDecimal.class)))
                .thenReturn(Account.restore(1L, "Alice", new BigDecimal("1000.00")));

        String body = "{\"name\":\"Alice\",\"initialBalance\":1000.00}";

        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Alice"))
                .andExpect(jsonPath("$.balance").value(1000.00));
    }

    @Test
    void deveRetornar400QuandoNomeAusente() throws Exception {
        String body = "{\"initialBalance\":1000.00}";

        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.fieldErrors", org.hamcrest.Matchers.not(org.hamcrest.Matchers.empty())));
    }

    @Test
    void deveRetornar404QuandoContaNaoExiste() throws Exception {
        when(getAccountUseCase.getById(99L))
                .thenThrow(new ResourceNotFoundException("Conta 99 nao encontrada"));

        mockMvc.perform(get("/api/accounts/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}
