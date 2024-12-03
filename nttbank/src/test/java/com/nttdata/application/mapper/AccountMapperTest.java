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
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
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

    @Test
    void testToEntity_withNullUserId_throwsException() {
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setId(1L);
        accountDTO.setUserId(null);
        accountDTO.setType(AccountType.CORRENTE);
        accountDTO.setBalance(1000.0);
        accountDTO.setTransactions(new ArrayList<>());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            accountMapper.toEntity(accountDTO);
        });
        assertEquals("User ID não pode ser nulo", exception.getMessage());
    }
    @Test
    void testToEntity_withNullTransactions() {
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setId(1L);
        accountDTO.setUserId(1L);
        accountDTO.setType(AccountType.CORRENTE);
        accountDTO.setBalance(1000.0);
        accountDTO.setTransactions(null);

        User user = new User();
        user.setId(1L);
        when(userService.findById(accountDTO.getUserId())).thenReturn(user);

        Account account = accountMapper.toEntity(accountDTO);

        assertNotNull(account);
        assertEquals(1L, account.getId());
        assertEquals(AccountType.CORRENTE, account.getType());
        assertEquals(1000.0, account.getBalance());

        assertNotNull(account.getUser());
        assertEquals(1L, account.getUser().getId());

        assertNotNull(account.getTransactions());
        assertTrue(account.getTransactions().isEmpty());
    }

}
