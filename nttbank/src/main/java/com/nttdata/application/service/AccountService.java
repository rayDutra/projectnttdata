package com.nttdata.application.service;

import com.nttdata.application.mapper.AccountMapper;
import com.nttdata.domain.entity.Account;
import com.nttdata.domain.entity.User;
import com.nttdata.domain.enums.AccountType;
import com.nttdata.dto.AccountDTO;
import com.nttdata.infrastructure.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final CurrencyConversionService currencyConversionService;
    private final AccountMapper accountMapper;

    @Autowired
    public AccountService(CurrencyConversionService currencyConversionService,
                          AccountRepository accountRepository,
                          AccountMapper accountMapper) {
        this.currencyConversionService = currencyConversionService;
        this.accountRepository = accountRepository;
        this.accountMapper = accountMapper;
    }

    public void updateCurrencyBalance(Account account) {
        if (account.getBalance() != null) {
            String baseCurrency = "BRL";
            account.setCurrencyBalance(currencyConversionService.convertToCurrencyBalance(account.getBalance(), baseCurrency));
        }
    }

    private void validateUniqueAccountType(User user, AccountType accountType) {
        Account existingAccount = accountRepository.findByUserAndType(user, accountType);
        if (existingAccount != null) {
            throw new IllegalArgumentException("O usuário já possui uma conta do tipo " + accountType);
        }
    }

    public boolean existsByUserIdAndType(Long userId, AccountType type) {
        return accountRepository.existsByUserIdAndType(userId, type);
    }

    @Transactional
    public Account save(User user, AccountDTO accountDTO) {
        validateUniqueAccountType(user, accountDTO.getType());
        Account account = accountMapper.toEntity(accountDTO);
        account.setUser(user);
        return accountRepository.save(account);
    }

    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    public Account findById(Long id) {
        Optional<Account> account = accountRepository.findById(id);
        return account.orElse(null);
    }

    public Account update(Long id, AccountDTO accountDTO) {
        Account existingAccount = findById(id);
        if (existingAccount != null) {
            validateUniqueAccountType(existingAccount.getUser(), accountDTO.getType());
            Account account = accountMapper.toEntity(accountDTO);
            account.setId(id);
            return accountRepository.save(account);
        }
        return null;
    }

    public void deactivate(Long id) {
        Account account = findById(id);
        if (account != null) {
            account.setActive(false);
            accountRepository.save(account);
        }
    }
    @Transactional
    public void delete(Long id) {
        Account account = findById(id);
        if (account == null) {
            throw new IllegalArgumentException("Conta não encontrada.");
        }
        accountRepository.delete(account);
    }

}
