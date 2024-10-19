package com.example.tasktwo.Repository;

import com.example.tasktwo.Entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query(value = "SELECT * FROM transaction where user_id =:userId AND merchant_id =:merchantId", nativeQuery = true)
    List<Transaction> findRecentTransactionsByUserIdAndMerchantId(Long userId, Long merchantId);

}



