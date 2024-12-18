package com.nttdata.application.service;

import com.nttdata.infrastructure.mapper.AccountMapper;
import com.nttdata.application.service.impls.AccountServiceImpl;
import com.nttdata.domain.entity.Account;
import com.nttdata.domain.entity.User;
import com.nttdata.domain.enums.AccountType;
import com.nttdata.application.dto.AccountDTO;
import com.nttdata.infrastructure.repository.AccountRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AccountService implements AccountServiceImpl {

    private final AccountRepository accountRepository;
    private final CurrencyConversionService currencyConversionService;
    private final AccountMapper accountMapper;

    private static final Logger logger = LoggerFactory.getLogger(AccountService.class);

    @Autowired
    public AccountService(CurrencyConversionService currencyConversionService,
                          AccountRepository accountRepository,
                          AccountMapper accountMapper) {
        this.currencyConversionService = currencyConversionService;
        this.accountRepository = accountRepository;
        this.accountMapper = accountMapper;
    }

    @Override
    public boolean existsByUserIdAndType(Long userId, AccountType type) {
        return accountRepository.existsByUserIdAndType(userId, type);
    }

    @Override
    @Transactional
    public Account save(User user, AccountDTO accountDTO) {
        if (user == null) {
            throw new EntityNotFoundException("Usuário não encontrado para o ID: " + accountDTO.getUserId());
        }
        if (existsByUserIdAndType(user.getId(), accountDTO.getType())) {
            throw new IllegalArgumentException("Já existe uma conta do tipo '" + accountDTO.getType() + "' para este usuário.");
        }

        try {
            validateUniqueAccountType(user, accountDTO.getType());
            Account account = accountMapper.toEntity(accountDTO);
            account.setUser(user);
            Account savedAccount = accountRepository.save(account);

            logger.info("Conta criada com sucesso para o usuário: " + user.getId());
            return savedAccount;
        } catch (Exception e) {
            logger.error("Erro ao criar a conta para o usuário: " + user.getId(), e);
            throw new RuntimeException("Erro ao criar a conta", e);
        }
    }

    @Override
    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    @Override
    public Account findById(Long id) {
        return accountRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Conta não encontrada para o ID: " + id));
    }

    @Override
    public Account update(Long id, AccountDTO accountDTO) {
        Account existingAccount = findById(id);
        validateUniqueAccountType(existingAccount.getUser(), accountDTO.getType());
        Account account = accountMapper.toEntity(accountDTO);
        account.setId(id);
        return accountRepository.save(account);
    }

    @Override
    public void deactivate(Long id) {
        Account account = findById(id);
        account.setActive(false);
        accountRepository.save(account);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Account account = findById(id);
        accountRepository.delete(account);
    }

    private void validateUniqueAccountType(User user, AccountType accountType) {
        Account existingAccount = accountRepository.findByUserAndType(user, accountType);
        if (existingAccount != null) {
            throw new IllegalArgumentException("O usuário já possui uma conta do tipo " + accountType);
        }
    }
}
