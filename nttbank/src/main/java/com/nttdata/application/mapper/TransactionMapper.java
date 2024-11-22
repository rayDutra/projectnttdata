package com.nttdata.application.mapper;

import com.nttdata.domain.entity.Account;
import com.nttdata.domain.entity.Transaction;
import com.nttdata.domain.enums.TransactionType;
import com.nttdata.dto.TransactionDTO;

public class TransactionMapper {

    public static TransactionDTO toDTO(Transaction transaction) {
        Long accountId = null;
        if (transaction.getAccount() != null) {
            accountId = transaction.getAccount().getId();
        }

        return new TransactionDTO(
            transaction.getId(),
            transaction.getType().name(),
            transaction.getAmount(),
            transaction.getDate(),
            accountId
        );
    }


    public static Transaction toEntity(TransactionDTO transactionDTO, Account account) {
        return new Transaction(
            TransactionType.valueOf(transactionDTO.getType()),
            transactionDTO.getAmount(),
            transactionDTO.getDate(),
            account
        );
    }
}


