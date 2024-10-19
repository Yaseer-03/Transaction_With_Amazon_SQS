package com.example.tasktwo.Entity;

import com.example.tasktwo.Enums.CreditDebitIndicator;
import com.example.tasktwo.Enums.CurrencyCode;
import com.example.tasktwo.Enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Objects;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Enumerated(EnumType.STRING)
    private CreditDebitIndicator creditDebitIndicator;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    private Long time;

    private double amount;

    @Enumerated(EnumType.STRING)
    private CurrencyCode currencyCode;

    @ManyToOne
    @JoinColumn(name = "merchant_id")
    private Merchant merchant;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "transaction", cascade = CascadeType.ALL)
    private List<Rewards> rewards;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}