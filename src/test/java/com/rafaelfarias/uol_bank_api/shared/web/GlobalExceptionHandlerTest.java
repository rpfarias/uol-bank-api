package com.rafaelfarias.uol_bank_api.shared.web;

import com.rafaelfarias.uol_bank_api.shared.exception.BusinessRuleException;
import com.rafaelfarias.uol_bank_api.shared.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void deveRetornar404QuandoRecursoNaoEncontrado() {
        HttpServletRequest request = new MockHttpServletRequest("GET", "/api/accounts/99");

        ResponseEntity<ApiError> response =
                handler.handleNotFound(new ResourceNotFoundException("Conta nao encontrada"), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(404);
        assertThat(response.getBody().message()).isEqualTo("Conta nao encontrada");
        assertThat(response.getBody().path()).isEqualTo("/api/accounts/99");
        assertThat(response.getBody().fieldErrors()).isEmpty();
    }

    @Test
    void deveRetornar422QuandoRegraDeNegocioViolada() {
        HttpServletRequest request = new MockHttpServletRequest("POST", "/api/transfers");

        ResponseEntity<ApiError> response =
                handler.handleBusinessRule(new BusinessRuleException("Saldo insuficiente"), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(422);
        assertThat(response.getBody().message()).isEqualTo("Saldo insuficiente");
    }
}
