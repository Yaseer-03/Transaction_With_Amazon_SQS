package com.example.tasktwo.Repository;

import com.example.tasktwo.Entity.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MerchantRepository extends JpaRepository<Merchant, Long> {
    void deleteByMerchantId(long merchantId);
    boolean existsByMerchantEmail(String merchantEmail);
}
