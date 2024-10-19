package com.example.tasktwo.Entity;

import com.example.tasktwo.Enums.*;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRequest {

    private Long userId;

    @Enumerated(EnumType.STRING)
    private CreditDebitIndicator creditDebitIndicator;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    private double amount;

    @Enumerated(EnumType.STRING)
    private CurrencyCode currencyCode;

    private Long merchantId;

    private String merchantName;

    @Enumerated(EnumType.STRING)
    private MerchantCategoryCode merchantCategoryCode;
}
