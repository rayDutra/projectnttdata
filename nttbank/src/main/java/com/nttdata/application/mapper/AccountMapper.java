package com.nttdata.application.mapper;

import com.nttdata.application.service.CurrencyConversionService;
import com.nttdata.application.service.UserService;
import com.nttdata.domain.entity.Account;
import com.nttdata.domain.entity.CurrencyBalance;
import com.nttdata.dto.AccountDTO;
import com.nttdata.dto.CurrencyBalanceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class AccountMapper {

    private final UserService userService;
    private final CurrencyConversionService currencyConversionService;

    @Autowired
    private TransactionMapper transactionMapper;

    @Autowired
    public AccountMapper(UserService userService, CurrencyConversionService currencyConversionService) {
        this.userService = userService;
        this.currencyConversionService = currencyConversionService;
    }

    public static Account toEntity(AccountDTO accountDTO) {
        if (accountDTO == null) {
            return null;
        }
        return new Account(
            accountDTO.getId(),
            accountDTO.getType(),
            accountDTO.getBalance(),
            null
        );
    }


    public AccountDTO toDTO(Account account) {
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
        accountDTO.setUserId(account.getUser() != null ? account.getUser().getId() : null);
        accountDTO.setCurrencyBalance(currencyBalanceDTO);

        if (account.getTransactions() != null) {
            accountDTO.setTransactions(account.getTransactions()
                .stream()
                .map(transactionMapper::toDTO)
                .collect(Collectors.toList()));
        }

        return accountDTO;
    }

    public static CurrencyBalanceDTO toCurrencyBalanceDTO(CurrencyBalance currencyBalance) {
        if (currencyBalance == null) {
            return null;
        }
        return new CurrencyBalanceDTO(
            currencyBalance.getBalanceReal(),
            currencyBalance.getBalanceDolar(),
            currencyBalance.getBalanceEuro(),
            currencyBalance.getBalanceIenes()
        );
    }
}

