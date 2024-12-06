package com.nttdata.application.mapper;

import com.nttdata.domain.entity.Account;
import com.nttdata.domain.entity.Transaction;
import com.nttdata.domain.enums.TransactionCategory;
import com.nttdata.domain.enums.TransactionType;
import com.nttdata.dto.TransactionDTO;
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

        // Converta a data para LocalDateTime, se ela estiver presente no DTO.
        LocalDateTime localDateTime = null;
        if (transactionDTO.getDate() != null) {
            localDateTime = transactionDTO.getDate().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        } else {
            // Caso a data não esteja presente, use a data atual (padrão do construtor)
            localDateTime = LocalDateTime.now();
        }

        // Criação da entidade Transaction com todos os dados
        return new Transaction(
            type,               // Tipo da transação
            category,           // Categoria da transação
            transactionDTO.getAmount(),  // Valor da transação
            localDateTime,      // Data da transação
            account             // Conta associada
        );
    }


}

