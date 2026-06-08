package com.rafaelfarias.uol_bank_api.movement.domain;

/**
 * Natureza de uma movimentação na perspectiva da conta consultada.
 */
public enum MovementType {
    /** Saída de valor (a conta é a origem da transferência). */
    DEBIT,
    /** Entrada de valor (a conta é o destino da transferência). */
    CREDIT
}
