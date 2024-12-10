package com.nttdata.web.controller;

import com.nttdata.application.mapper.TransactionMapper;
import com.nttdata.application.service.AccountService;
import com.nttdata.application.service.impls.TransactionServiceImpl;
import com.nttdata.domain.entity.Account;
import com.nttdata.domain.entity.Transaction;
import com.nttdata.dto.TransactionDTO;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.persistence.EntityNotFoundException;
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
            throw new EntityNotFoundException("Account not found");
        }
        Transaction transaction = transactionMapper.toEntity(transactionDTO, account);
        Transaction processedTransaction = transactionServiceImpl.processTransaction(transaction);
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionMapper.toDTO(processedTransaction));
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
        Transaction transaction = transactionServiceImpl.findById(id);
        return ResponseEntity.ok(transactionMapper.toDTO(transaction));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionDTO> updateTransaction(@PathVariable Long id, @RequestBody TransactionDTO transactionDTO) {
        Transaction updatedTransaction = transactionServiceImpl.update(id, transactionDTO);
        return ResponseEntity.ok(transactionMapper.toDTO(updatedTransaction));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        transactionServiceImpl.delete(id);
        return ResponseEntity.noContent().build();
    }
}
