package com.nttdata.application.mapper;

import com.nttdata.application.service.UserService;
import com.nttdata.domain.entity.Account;
import com.nttdata.domain.entity.User;
import com.nttdata.dto.AccountDTO;
import com.nttdata.dto.TransactionDTO;

import java.util.List;
import java.util.stream.Collectors;

public class AccountMapper {

    private static UserService userService;

    public static void setUserService(UserService userService) {
        AccountMapper.userService = userService;
    }

    public static AccountDTO toDTO(Account account) {
        if (account == null) {
            return null;
        }

        List<TransactionDTO> transactionDTOList = account.getTransactions() != null
            ? account.getTransactions().stream()
            .map(UserMapper::toTransactionDTO)
            .collect(Collectors.toList())
            : null;

        return new AccountDTO(
            account.getId(),
            account.getType(),
            account.getBalance(),
            account.getUser() != null ? account.getUser().getId() : null,
            transactionDTOList
        );
    }


    public static Account toEntity(AccountDTO accountDTO) {
        if (accountDTO == null) {
            return null;
        }

        Account account = new Account();
        account.setId(accountDTO.getId());
        account.setType(accountDTO.getType());
        account.setBalance(accountDTO.getBalance());

        if (accountDTO.getUserId() != null && userService != null) {
            User user = userService.findById(accountDTO.getUserId());
            account.setUser(user);
        }

        return account;
    }
}
