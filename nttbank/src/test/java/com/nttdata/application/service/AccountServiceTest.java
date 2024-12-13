package com.nttdata.application.service;

import com.nttdata.infrastructure.mapper.AccountMapper;
import com.nttdata.domain.entity.Account;
import com.nttdata.domain.entity.User;
import com.nttdata.domain.enums.AccountType;
import com.nttdata.application.dto.AccountDTO;
import com.nttdata.infrastructure.repository.AccountRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CurrencyConversionService currencyConversionService;

    @Mock
    private AccountMapper accountMapper;

    @InjectMocks
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testExistsByUserIdAndType() {
        Long userId = 1L;
        AccountType type = AccountType.CORRENTE;

        when(accountRepository.existsByUserIdAndType(userId, type)).thenReturn(true);

        assertTrue(accountService.existsByUserIdAndType(userId, type));
        verify(accountRepository).existsByUserIdAndType(userId, type);
    }

    @Test
    void testSave() {
        User user = new User();
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setType(AccountType.CORRENTE);

        Account account = new Account();
        account.setUser(user);

        when(accountMapper.toEntity(accountDTO)).thenReturn(account);
        when(accountRepository.save(account)).thenReturn(account);

        Account savedAccount = accountService.save(user, accountDTO);

        assertNotNull(savedAccount);
        verify(accountMapper).toEntity(accountDTO);
        verify(accountRepository).save(account);
    }

    @Test
    void testFindAll() {
        List<Account> accounts = List.of(new Account(), new Account());
        when(accountRepository.findAll()).thenReturn(accounts);

        List<Account> result = accountService.findAll();

        assertEquals(2, result.size());
        verify(accountRepository).findAll();
    }

    @Test
    void testFindById() {
        Long accountId = 1L;
        Account account = new Account();
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        Account result = accountService.findById(accountId);

        assertNotNull(result);
        verify(accountRepository).findById(accountId);
    }

    @Test
    void testFindById_NotFound() {
        Long accountId = 1L;
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
            () -> accountService.findById(accountId));

        assertEquals("Conta não encontrada para o ID: " + accountId, exception.getMessage());
        verify(accountRepository).findById(accountId);
    }

    @Test
    void testUpdate() {
        Long accountId = 1L;
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setType(AccountType.CORRENTE);

        Account existingAccount = new Account();
        existingAccount.setUser(new User());

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(existingAccount));
        when(accountMapper.toEntity(accountDTO)).thenReturn(existingAccount);
        when(accountRepository.save(existingAccount)).thenReturn(existingAccount);

        Account updatedAccount = accountService.update(accountId, accountDTO);

        assertNotNull(updatedAccount);
        verify(accountRepository).findById(accountId);
        verify(accountMapper).toEntity(accountDTO);
        verify(accountRepository).save(existingAccount);
    }

    @Test
    void testDeactivate() {
        Long accountId = 1L;
        Account account = new Account();

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountRepository.save(account)).thenReturn(account);

        accountService.deactivate(accountId);

        assertFalse(account.isActive());
        verify(accountRepository).findById(accountId);
        verify(accountRepository).save(account);
    }

    @Test
    void testDelete() {
        Long accountId = 1L;
        Account account = new Account();

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        accountService.delete(accountId);

        verify(accountRepository).findById(accountId);
        verify(accountRepository).delete(account);
    }

    @Test
    void testValidateUniqueAccountType_ThrowsException() {
        User user = new User();
        AccountType accountType = AccountType.CORRENTE;

        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setType(accountType);

        when(accountRepository.findByUserAndType(user, accountType)).thenReturn(new Account());

        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> accountService.save(user, accountDTO));

        assertTrue(exception.getCause() instanceof IllegalArgumentException);
        assertEquals("Erro ao criar a conta", exception.getMessage());

        verify(accountRepository).findByUserAndType(user, accountType);
    }
}
