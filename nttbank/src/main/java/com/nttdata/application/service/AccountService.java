package com.nttdata.application.service;

import com.nttdata.application.mapper.AccountMapper;
import com.nttdata.domain.entity.Account;
import com.nttdata.dto.AccountDTO;
import com.nttdata.infrastructure.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    public Account save(Account account) {
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
            Account account = AccountMapper.toEntity(accountDTO);
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
}
