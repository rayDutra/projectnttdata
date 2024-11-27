package com.nttdata.application.mapper;

import com.nttdata.application.service.CurrencyConversionService;
import com.nttdata.application.service.UserService;
import com.nttdata.domain.entity.Account;
import com.nttdata.domain.entity.CurrencyBalance;
import com.nttdata.domain.entity.User;
import com.nttdata.dto.AccountDTO;
import com.nttdata.dto.CurrencyBalanceDTO;

public class AccountMapper {

    private static UserService userService;
    private static CurrencyConversionService currencyConversionService;

    public static void setUserService(UserService userService) {
        AccountMapper.userService = userService;
    }

    public static AccountDTO toDTO(Account account) {
        if (account == null) {
            return null;
        }
        CurrencyBalance currencyBalance = currencyConversionService.convertToCurrencyBalance(account.getBalance());

        CurrencyBalanceDTO currencyBalanceDTO = toCurrencyBalanceDTO(currencyBalance);

        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setId(account.getId());
        accountDTO.setType(account.getType());
        accountDTO.setBalance(account.getBalance());
        accountDTO.setUserId(account.getUser() != null ? account.getUser().getId() : null);
        accountDTO.setCurrencyBalance(currencyBalanceDTO);

        return accountDTO;
    }

    public static CurrencyBalanceDTO toCurrencyBalanceDTO(CurrencyBalance currencyBalance) {
        if (currencyBalance == null) {
            return null;
        }

        CurrencyBalanceDTO currencyBalanceDTO = new CurrencyBalanceDTO(
            currencyBalance.getBalanceReal(),
            currencyBalance.getBalanceDolar(),
            currencyBalance.getBalanceEuro(),
            currencyBalance.getBalanceIenes()
        );

        return currencyBalanceDTO;
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
