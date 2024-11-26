package com.nttdata.application.mapper;

import com.nttdata.domain.entity.Account;
import com.nttdata.domain.entity.Transaction;
import com.nttdata.domain.entity.User;
import com.nttdata.dto.TransactionDTO;
import com.nttdata.dto.UserDTO;
import com.nttdata.dto.AccountDTO;

import java.util.stream.Collectors;

public class UserMapper {

    public static User toEntity(UserDTO userDTO) {
        if (userDTO == null) {
            return null;
        }
        return new User(
            userDTO.getId(),
            userDTO.getName(),
            userDTO.getEmail(),
            userDTO.getLogin(),
            userDTO.getPassword(),
            userDTO.getDate()
        );
    }

    public static UserDTO toDTO(User user) {
        if (user == null) {
            return null;
        }
        return new UserDTO(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getLogin(),
            user.getPassword(),
            user.getDate(),
            user.getAccounts() != null
                ? user.getAccounts().stream()
                .map(UserMapper::toAccountDTO)
                .collect(Collectors.toList())
                : null
        );
    }

    public static AccountDTO toAccountDTO(Account account) {
        if (account == null) {
            return null;
        }
        return new AccountDTO(
            account.getId(),
            account.getType(),
            account.getBalance(),
            account.getUser() != null ? account.getUser().getId() : null,
            account.getTransactions() != null
                ? account.getTransactions().stream()
                .map(UserMapper::toTransactionDTO)
                .collect(Collectors.toList())
                : null
        );
    }

    public static TransactionDTO toTransactionDTO(Transaction transaction) {
        if (transaction == null) {
            return null;
        }
        return new TransactionDTO(
            transaction.getId(),
            transaction.getType(),
            transaction.getCategory(),
            transaction.getAmount(),
            transaction.getDate(),
            transaction.getAccount() != null ? transaction.getAccount().getId() : null
        );
    }

    public static void updateEntityFromDTO(UserDTO userDTO, User user) {
        if (userDTO.getName() != null) {
            user.setName(userDTO.getName());
        }
        if (userDTO.getEmail() != null) {
            user.setEmail(userDTO.getEmail());
        }
        if (userDTO.getLogin() != null) {
            user.setLogin(userDTO.getLogin());
        }
        if (userDTO.getPassword() != null) {
            user.setPassword(userDTO.getPassword());
        }
        if (userDTO.getDate() != null) {
            user.setDate(userDTO.getDate());
        }
    }
}
