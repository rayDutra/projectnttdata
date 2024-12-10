package com.nttdata.application.mapper;
import java.util.Date;
import java.time.ZoneId;

import com.nttdata.application.service.CurrencyConversionService;
import com.nttdata.domain.entity.Account;
import com.nttdata.domain.entity.CurrencyBalance;
import com.nttdata.domain.entity.Transaction;
import com.nttdata.domain.entity.User;
import com.nttdata.dto.CurrencyBalanceDTO;
import com.nttdata.dto.TransactionDTO;
import com.nttdata.dto.UserDTO;
import com.nttdata.dto.AccountDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    private final CurrencyConversionService currencyConversionService;

    @Autowired
    public UserMapper(CurrencyConversionService currencyConversionService) {
        this.currencyConversionService = currencyConversionService;
    }

    public User toEntity(UserDTO userDTO) {
        if (userDTO == null) {
            return null;
        }
        User user = new User();
        user.setId(userDTO.getId());
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setLogin(userDTO.getLogin());
        user.setPassword(userDTO.getPassword());

        if (userDTO.getDate() != null) {
            LocalDateTime localDateTime = userDTO.getDate().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
            user.setDate(localDateTime);
        }

        return user;
    }

    public UserDTO toDTO(User user) {
        if (user == null) {
            return null;
        }

        Date date = null;
        if (user.getDate() != null) {
            date = Date.from(user.getDate()
                .atZone(ZoneId.systemDefault())
                .toInstant());
        }

        return new UserDTO(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getLogin(),
            user.getPassword(),
            date,
            user.getAccounts() != null
                ? user.getAccounts().stream()
                .map(this::toAccountDTO)
                .collect(Collectors.toList())
                : null
        );
    }
    public AccountDTO toAccountDTO(Account account) {
        if (account == null) {
            return null;
        }

        String baseCurrency = "BRL";

        CurrencyBalance currencyBalance = currencyConversionService.convertToCurrencyBalance(account.getBalance(), baseCurrency);
        CurrencyBalanceDTO currencyBalanceDTO = AccountMapper.toCurrencyBalanceDTO(currencyBalance);

        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setId(account.getId());
        accountDTO.setType(account.getType());
        accountDTO.setBalance(account.getBalance());
        accountDTO.setNumber(account.getNumber());
        accountDTO.setUserId(account.getUser() != null ? account.getUser().getId() : null);
        accountDTO.setCurrencyBalance(currencyBalanceDTO);

        if (account.getTransactions() != null) {
            accountDTO.setTransactions(
                account.getTransactions().stream()
                    .map(this::toTransactionDTO)
                    .collect(Collectors.toList())
            );
        }

        return accountDTO;
    }

    public TransactionDTO toTransactionDTO(Transaction transaction) {
        if (transaction == null) {
            return null;
        }
        Date transactionDate = Date.from(transaction.getDate().atZone(ZoneId.systemDefault()).toInstant());

        return new TransactionDTO(
            transaction.getId(),
            transaction.getType(),
            transaction.getCategory(),
            transaction.getAmount(),
            transactionDate,
            transaction.getAccount() != null ? transaction.getAccount().getId() : null
        );
    }

    public void updateEntityFromDTO(UserDTO userDTO, User user) {
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
            LocalDateTime date = userDTO.getDate()
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
            user.setDate(date);
        }
    }
}
