package com.nttdata.infrastructure.mapper;

import com.nttdata.domain.entity.Account;
import com.nttdata.domain.entity.Transaction;
import com.nttdata.domain.enums.TransactionCategory;
import com.nttdata.domain.enums.TransactionType;
import com.nttdata.application.dto.TransactionDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

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
        Date date = null;
        if (transaction.getDate() != null) {
            date = Date.from(transaction.getDate()
                .atZone(ZoneId.systemDefault())
                .toInstant());
        }

        return new TransactionDTO(
            transaction.getId(),
            transaction.getType(),
            transaction.getCategory(),
            transaction.getAmount(),
            date,
            accountId
        );
    }
    public Transaction toEntity(TransactionDTO transactionDTO, Account account) {
        if (transactionDTO == null || account == null) {
            throw new IllegalArgumentException("TransactionDTO e Account não podem ser nulos");
        }

        TransactionType type = transactionDTO.getType();
        TransactionCategory category = transactionDTO.getCategory();
        LocalDateTime localDateTime = null;
        if (transactionDTO.getDate() != null) {
            localDateTime = transactionDTO.getDate().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        } else {
            localDateTime = LocalDateTime.now();
        }

        return new Transaction(
            type,
            category,
            transactionDTO.getAmount(),
            localDateTime,
            account
        );
    }


}

