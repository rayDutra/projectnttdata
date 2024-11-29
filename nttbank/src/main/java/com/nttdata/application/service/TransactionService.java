package com.nttdata.application.service;

import com.nttdata.application.impls.TransactionServiceImpl;
import com.nttdata.domain.entity.Account;
import com.nttdata.domain.entity.Transaction;
import com.nttdata.dto.TransactionDTO;
import com.nttdata.infrastructure.repository.AccountRepository;
import com.nttdata.infrastructure.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        return transactionRepository.findById(id).orElse(null);
    }

    @Override
    public List<Transaction> findAll() {
        return transactionRepository.findAll();
    }
    @Override
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

    @Override
    public void delete(Long id) {
        Transaction transaction = findById(id);
        if (transaction != null) {
            transactionRepository.delete(transaction);
        }
    }
    @Override
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
                if (account.getBalance() < transaction.getAmount() + 5) {
                    throw new IllegalStateException("Saldo insuficiente para realizar o saque com desconto.");
                }
                account.setBalance(account.getBalance() - (transaction.getAmount() + 5));
                break;

            case TRANSFERENCIA:
                if (transaction.getAmount() <= 0) {
                    throw new IllegalArgumentException("O valor da transferência deve ser maior que zero.");
                }
                if (account.getBalance() < transaction.getAmount() + 10) {
                    throw new IllegalStateException("Saldo insuficiente para realizar a transferência com desconto.");
                }
                account.setBalance(account.getBalance() - (transaction.getAmount() + 10));
                break;

            case PIX:
                if (transaction.getAmount() <= 0) {
                    throw new IllegalArgumentException("O valor do PIX deve ser maior que zero.");
                }
                account.setBalance(account.getBalance() - transaction.getAmount());
                break;

            case BOLETO:
                if (transaction.getAmount() <= 0) {
                    throw new IllegalArgumentException("O valor do boleto deve ser maior que zero.");
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



}
