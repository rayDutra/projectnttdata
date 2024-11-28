package com.nttdata.application.mapper;

import com.nttdata.domain.entity.Account;
import com.nttdata.domain.entity.Transaction;
import com.nttdata.dto.TransactionDTO;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {

    public TransactionDTO toDTO(Transaction transaction) {
        if (transaction == null) {
            return null;
        }

        Long accountId = null;
        if (transaction.getAccount() != null) {
            accountId = transaction.getAccount().getId();
        }

        return new TransactionDTO(
            transaction.getId(),
            transaction.getType(),
            transaction.getCategory(),
            transaction.getAmount(),
            transaction.getDate(),
            accountId
        );
    }

    public Transaction toEntity(TransactionDTO transactionDTO, Account account) {
        if (transactionDTO == null || account == null) {
            return null;
        }

        return new Transaction(
            transactionDTO.getType(),
            transactionDTO.getCategory(),
            transactionDTO.getAmount(),
            transactionDTO.getDate(),
            account
        );
    }
}

