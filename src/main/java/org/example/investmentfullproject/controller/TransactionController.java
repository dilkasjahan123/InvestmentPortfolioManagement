package org.example.investmentfullproject.controller;

import org.example.investmentfullproject.model.Transaction;
import org.example.investmentfullproject.repository.TransactionRepository;
import org.example.investmentfullproject.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transaction")
@CrossOrigin("*")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private TransactionRepository transactionRepository;

    @PostMapping("/add")
    public Transaction addTransaction(
            @RequestBody Transaction transaction){

        return transactionService
                .createTransaction(
                        transaction
                );
    }

    @GetMapping("/all")
    public List<Transaction> getAllTransactions(){

        return transactionRepository.findAll();
    }
    @GetMapping("/count")
    public long getTransactionCount(){

        return transactionService.getTransactionCount();
    }
}