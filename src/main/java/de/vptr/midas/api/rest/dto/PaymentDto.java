package de.vptr.midas.api.rest.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class PaymentDto {
    @NotNull(message = "Target account ID is required")
    public Long targetAccountId;

    @NotNull(message = "Source account ID is required")
    public Long sourceAccountId;

    @NotNull(message = "User ID is required")
    public Long userId;

    @NotBlank(message = "Comment is required")
    public String comment;

    @NotNull(message = "Date is required")
    public LocalDate date;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.00", message = "Amount must be positive")
    public BigDecimal amount;
}
