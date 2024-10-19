package com.example.tasktwo.Controller;

import com.example.tasktwo.Entity.Transaction;
import com.example.tasktwo.Entity.TransactionRequest;
import com.example.tasktwo.Service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
public class TransactionRequestController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping("/webhook/transaction")
    public String performingTransaction(@RequestBody TransactionRequest transactionRequest) {
        return transactionService.makeTransaction(transactionRequest);
    }

    @GetMapping("/webhook/transactions")
    public List<Transaction> getAllTransactions() {
        return transactionService.getAllTransactions();
    }
}

