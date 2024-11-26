package com.nttdata.application.service;

import com.nttdata.domain.entity.Account;
import com.nttdata.domain.entity.Transaction;
import com.nttdata.domain.enums.TransactionCategory;
import com.nttdata.domain.enums.TransactionType;
import com.nttdata.dto.TransactionDTO;
import com.nttdata.infrastructure.repository.AccountRepository;
import com.nttdata.infrastructure.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;
    public Transaction save(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    public List<Transaction> findAll() {
        return transactionRepository.findAll();
    }

    public Transaction findById(Long id) {
        return transactionRepository.findById(id).orElse(null);
    }

    public Transaction update(Long id, TransactionDTO transactionDTO) {
        Transaction transaction = findById(id);
        if (transaction != null) {
            transaction.setType(transactionDTO.getType());
            transaction.setCategory(transactionDTO.getCategory());
            transaction.setAmount(transactionDTO.getAmount());
            transaction.setDate(transactionDTO.getDate());
            return transactionRepository.save(transaction);
        }
        return null;
    }

    public void delete(Long id) {
        Transaction transaction = findById(id);
        if (transaction != null) {
            transactionRepository.delete(transaction);
        }
    }
    public Transaction processTransaction(Transaction transaction) {
        Account account = transaction.getAccount();
        if (account == null || account.getId() == null) {
            throw new IllegalArgumentException("Conta associada à transação não encontrada.");
        }

        switch (transaction.getType()) {
            case DEPOSITO:
                if (transaction.getAmount() <= 0) {
                    throw new IllegalArgumentException("O valor do depósito deve ser maior que zero.");
                }
                account.setBalance(account.getBalance() + transaction.getAmount());
                break;

            case SAQUE:
                if (transaction.getAmount() <= 0) {
                    throw new IllegalArgumentException("O valor do saque deve ser maior que zero.");
                }
                if (account.getBalance() < transaction.getAmount()) {
                    throw new IllegalStateException("Saldo insuficiente.");
                }
                account.setBalance(account.getBalance() - transaction.getAmount());
                break;

            default:
                throw new IllegalArgumentException("Tipo de transação inválido.");
        }

        accountRepository.save(account);

        transaction.setDate(new Date());
        return transactionRepository.save(transaction);
    }

    public List<Transaction> findByUserId(Long userId) {
        return transactionRepository.findByUserId(userId);
    }


}
