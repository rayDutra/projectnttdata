package com.nttdata.application.mapper;

import com.nttdata.domain.entity.Account;
import com.nttdata.domain.entity.Transaction;
import com.nttdata.dto.TransactionDTO;

public class TransactionMapper {

    public static TransactionDTO toDTO(Transaction transaction) {
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

    public static Transaction toEntity(TransactionDTO transactionDTO, Account account) {
        return new Transaction(
            transactionDTO.getType(),
            transactionDTO.getCategory(),
            transactionDTO.getAmount(),
            transactionDTO.getDate(),
            account
        );
    }
}
