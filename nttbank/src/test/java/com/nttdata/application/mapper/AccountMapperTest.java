package com.nttdata.application.mapper;

import com.nttdata.application.service.CurrencyConversionService;
import com.nttdata.application.service.UserService;
import com.nttdata.domain.entity.Account;
import com.nttdata.domain.entity.CurrencyBalance;
import com.nttdata.domain.entity.User;
import com.nttdata.domain.enums.AccountType;
import com.nttdata.dto.AccountDTO;
import com.nttdata.dto.CurrencyBalanceDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class AccountMapperTest {

    @InjectMocks
    private AccountMapper accountMapper;

    @Mock
    private UserService userService;

    @Mock
    private CurrencyConversionService currencyConversionService;

    @InjectMocks
    private TransactionMapper transactionMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testToEntity_withValidAccountDTO() {
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setId(1L);
        accountDTO.setType(AccountType.CORRENTE);
        accountDTO.setBalance(Double.valueOf(1000));
        accountDTO.setTransactions(Collections.emptyList());

        Account account = accountMapper.toEntity(accountDTO);

        assertNotNull(account);
        assertEquals(1L, account.getId());
        assertEquals(AccountType.CORRENTE, account.getType());
        assertEquals(Double.valueOf(1000), account.getBalance());
        assertTrue(account.getTransactions().isEmpty());
    }
    @Disabled
    @Test
    void testToDTO_withValidAccount() {
        Account account = new Account();
        account.setId(1L);
        account.setType(AccountType.CORRENTE);
        account.setBalance(1000.0);
        account.setUser(new User(1L, "Nome", "email@example.com", "login", "senha", new Date()));
        account.setTransactions(new ArrayList<>());

        CurrencyBalance currencyBalance = new CurrencyBalance(1000.0, 200.0, 150.0, 500.0);
        when(currencyConversionService.convertToCurrencyBalance(any(), any())).thenReturn(currencyBalance);

        AccountDTO accountDTO = accountMapper.toDTO(account);

        assertNotNull(accountDTO);
        assertEquals(1L, accountDTO.getId());
        assertEquals(AccountType.CORRENTE, accountDTO.getType());
        assertEquals(1000.0, accountDTO.getBalance());
        assertNotNull(accountDTO.getCurrencyBalance());
        assertEquals("R$ 1000,00", accountDTO.getCurrencyBalance().getBalanceReal());
        assertEquals(1L, accountDTO.getUserId());
        assertTrue(accountDTO.getTransactions().isEmpty());
    }



    @Test
    void testToEntity_withNullAccountDTO_throwsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            accountMapper.toEntity(null);
        });
        assertEquals("AccountDTO não pode ser nulo", exception.getMessage());
    }


    @Test
    void testToDTO_withNullAccount_returnsNull() {
        AccountDTO accountDTO = accountMapper.toDTO(null);
        assertNull(accountDTO);
    }

    @Test
    void testToCurrencyBalanceDTO_withValidCurrencyBalance() {
        CurrencyBalance currencyBalance = new CurrencyBalance(1000.0, 200.0, 150.0, 500.0);

        CurrencyBalanceDTO currencyBalanceDTO = AccountMapper.toCurrencyBalanceDTO(currencyBalance);

        assertNotNull(currencyBalanceDTO);
        assertEquals("R$ 1000,00", currencyBalanceDTO.getBalanceReal());
        assertEquals("$ 200,00", currencyBalanceDTO.getBalanceDolar());
        assertEquals("€ 150,00", currencyBalanceDTO.getBalanceEuro());
        assertEquals("¥ 500,00", currencyBalanceDTO.getBalanceIenes());
    }


    @Test
    void testToCurrencyBalanceDTO_withNullCurrencyBalance_returnsNull() {
        CurrencyBalanceDTO currencyBalanceDTO = AccountMapper.toCurrencyBalanceDTO(null);
        assertNull(currencyBalanceDTO);
    }
}
