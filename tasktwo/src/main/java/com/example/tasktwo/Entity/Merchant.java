package com.example.tasktwo.Entity;

import com.example.tasktwo.Enums.MerchantCategoryCode;
import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Merchant {
    @Id
    private long merchantId;

    @Column(nullable = false)
    private String merchantName;

    @Column(nullable = false)
    private String merchantEmail;

    @Enumerated(EnumType.STRING)
    private MerchantCategoryCode merchantCategoryCode;

}
