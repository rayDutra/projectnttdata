package com.nttdata.web.controller;

import com.nttdata.application.mapper.TransactionMapper;
import com.nttdata.application.service.AccountService;
import com.nttdata.application.impls.TransactionServiceImpl;
import com.nttdata.domain.entity.Account;
import com.nttdata.domain.entity.Transaction;
import com.nttdata.dto.TransactionDTO;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/transactions")
@SecurityRequirement(name = "bearer-key")
public class TransactionController {

    @Autowired
    private TransactionServiceImpl transactionServiceImpl;

    @Autowired
    private AccountService accountService;

    @Autowired
    private TransactionMapper transactionMapper;

    @PostMapping
    public ResponseEntity<TransactionDTO> createTransaction(@RequestBody TransactionDTO transactionDTO) {
        Account account = accountService.findById(transactionDTO.getAccountId());

        if (account == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        Transaction transaction = transactionMapper.toEntity(transactionDTO, account);
        Transaction processedTransaction = transactionServiceImpl.processTransaction(transaction);
        TransactionDTO transactionResponse = transactionMapper.toDTO(processedTransaction);
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionResponse);
    }

    @GetMapping
    public ResponseEntity<List<TransactionDTO>> getAllTransactions() {
        List<TransactionDTO> transactionsDTO = transactionServiceImpl.findAll().stream()
            .map(transactionMapper::toDTO)
            .collect(Collectors.toList());

        return ResponseEntity.ok(transactionsDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionDTO> getTransactionById(@PathVariable Long id) {
        var transaction = transactionServiceImpl.findById(id);
        if (transaction != null) {
            return ResponseEntity.ok(transactionMapper.toDTO(transaction));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionDTO> updateTransaction(@PathVariable Long id, @RequestBody TransactionDTO transactionDTO) {
        var updatedTransaction = transactionServiceImpl.update(id, transactionDTO);
        if (updatedTransaction != null) {
            return ResponseEntity.ok(transactionMapper.toDTO(updatedTransaction));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        transactionServiceImpl.delete(id);
        return ResponseEntity.noContent().build();
    }
}
