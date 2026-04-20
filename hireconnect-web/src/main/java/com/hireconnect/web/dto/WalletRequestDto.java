package com.hireconnect.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WalletRequestDto {

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "1.0", message = "Amount must be greater than 0")
    private Double amount;
}