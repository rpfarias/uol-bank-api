package com.rafaelfarias.uol_bank_api.account.adapter.in.web;

import com.rafaelfarias.uol_bank_api.account.adapter.in.web.dto.AccountResponse;
import com.rafaelfarias.uol_bank_api.account.adapter.in.web.dto.CreateAccountRequest;
import com.rafaelfarias.uol_bank_api.account.application.port.in.CreateAccountUseCase;
import com.rafaelfarias.uol_bank_api.account.application.port.in.GetAccountUseCase;
import com.rafaelfarias.uol_bank_api.account.domain.Account;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@Tag(name = "Contas", description = "Gestão e consulta de contas de clientes")
public class AccountController {

    private final CreateAccountUseCase createAccountUseCase;
    private final GetAccountUseCase getAccountUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cadastra uma nova conta")
    public AccountResponse create(@Valid @RequestBody CreateAccountRequest request) {
        Account account = createAccountUseCase.create(request.name(), request.initialBalance());
        return AccountResponse.from(account);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulta uma conta pelo id")
    public AccountResponse getById(@PathVariable Long id) {
        return AccountResponse.from(getAccountUseCase.getById(id));
    }

    @GetMapping
    @Operation(summary = "Lista todas as contas")
    public List<AccountResponse> getAll() {
        return getAccountUseCase.getAll().stream().map(AccountResponse::from).toList();
    }
}
