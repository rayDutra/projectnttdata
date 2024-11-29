package com.nttdata.application.mapper;

import com.nttdata.domain.entity.Account;
import com.nttdata.domain.entity.Transaction;
import com.nttdata.domain.enums.TransactionCategory;
import com.nttdata.domain.enums.TransactionType;
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

        TransactionType type = transactionDTO.getType();
        TransactionCategory category = transactionDTO.getCategory();

        Transaction transaction = new Transaction(
            type,
            category,
            transactionDTO.getAmount(),
            transactionDTO.getDate(),
            account
        );

        return transaction;
    }

}

