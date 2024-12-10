package com.nttdata.application.service;

import com.nttdata.application.service.impls.TransactionServiceImpl;
import com.nttdata.domain.entity.Account;
import com.nttdata.domain.entity.Transaction;
import com.nttdata.dto.TransactionDTO;
import com.nttdata.infrastructure.repository.AccountRepository;
import com.nttdata.infrastructure.repository.TransactionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
public class TransactionService implements TransactionServiceImpl {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public Transaction save(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    @Override
    public Transaction findById(Long id) {
        return transactionRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Transação não encontrada para o ID: " + id));
    }

    @Override
    public List<Transaction> findAll() {
        return transactionRepository.findAll();
    }

    @Override
    public Transaction update(Long id, TransactionDTO transactionDTO) {
        Transaction transaction = findById(id);
        transaction.setType(transactionDTO.getType());
        transaction.setCategory(transactionDTO.getCategory());
        transaction.setAmount(transactionDTO.getAmount());
        return transactionRepository.save(transaction);
    }

    @Override
    public void delete(Long id) {
        Transaction transaction = findById(id);
        transactionRepository.delete(transaction);
    }

    @Override
    public Transaction processTransaction(Transaction transaction) {
        Account account = transaction.getAccount();
        if (account == null || account.getId() == null) {
            throw new IllegalArgumentException("Conta associada à transação não encontrada.");
        }
        switch (transaction.getType()) {
            case DEPOSITO:
                validatePositiveAmount(transaction.getAmount(), "depósito");
                account.setBalance(account.getBalance() + transaction.getAmount());
                break;

            case SAQUE:
                validatePositiveAmount(transaction.getAmount(), "saque");
                if (account.getBalance() < transaction.getAmount() + 5) {
                    throw new IllegalStateException("Saldo insuficiente para realizar o saque com desconto.");
                }
                account.setBalance(account.getBalance() - (transaction.getAmount() + 5));
                break;

            case TRANSFERENCIA:
                validatePositiveAmount(transaction.getAmount(), "transferência");
                if (account.getBalance() < transaction.getAmount() + 10) {
                    throw new IllegalStateException("Saldo insuficiente para realizar a transferência com desconto.");
                }
                account.setBalance(account.getBalance() - (transaction.getAmount() + 10));
                break;

            case PIX:
                validatePositiveAmount(transaction.getAmount(), "PIX");
                account.setBalance(account.getBalance() - transaction.getAmount());
                break;

            case BOLETO:
                validatePositiveAmount(transaction.getAmount(), "boleto");
                account.setBalance(account.getBalance() - transaction.getAmount());
                break;

            default:
                throw new IllegalArgumentException("Tipo de transação inválido.");
        }

        accountRepository.save(account);
        transaction.setDate(convertToLocalDateTime(new Date()));
        return transactionRepository.save(transaction);
    }

    private void validatePositiveAmount(double amount, String operation) {
        if (amount <= 0) {
            throw new IllegalArgumentException("O valor do " + operation + " deve ser maior que zero.");
        }
    }

    public LocalDateTime convertToLocalDateTime(Date date) {
        return date.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime();
    }
}

