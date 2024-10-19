package com.example.tasktwo.Repository;

import com.example.tasktwo.Entity.Rewards;
import com.example.tasktwo.Entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface RewardRepository extends JpaRepository<Rewards, Long> {
    Rewards findByTransaction(Transaction lastTransaction);
}
