package com.nttdata.application.mapper;

import com.nttdata.application.service.CurrencyConversionService;
import com.nttdata.domain.entity.Account;
import com.nttdata.domain.entity.CurrencyBalance;
import com.nttdata.domain.entity.Transaction;
import com.nttdata.domain.entity.User;
import com.nttdata.domain.enums.AccountType;
import com.nttdata.domain.enums.TransactionCategory;
import com.nttdata.domain.enums.TransactionType;
import com.nttdata.application.dto.AccountDTO;
import com.nttdata.application.dto.TransactionDTO;
import com.nttdata.application.dto.UserDTO;
import com.nttdata.infrastructure.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class UserMapperTest {

    @Mock
    private CurrencyConversionService currencyConversionService;

    @InjectMocks
    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testToEntity_withValidUserDTO() {
        LocalDate localDate = LocalDate.now();
        Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        UserDTO userDTO = new UserDTO(1L, "John Doe", "john@example.com", "john123", "password", date, null);

        User user = userMapper.toEntity(userDTO);

        assertNotNull(user);
        assertEquals(1L, user.getId());
        assertEquals("John Doe", user.getName());
        assertEquals("john@example.com", user.getEmail());
        assertEquals("john123", user.getLogin());
        assertEquals("password", user.getPassword());

        LocalDateTime localDateTime = date.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime();

        assertEquals(localDate, localDateTime.toLocalDate(), "A data não coincide!");
    }

    @Test
    void testToEntity_withNullUserDTO() {
        assertNull(userMapper.toEntity(null));
    }

    @Test
    void testToDTO_withValidUser() throws Exception {
        String dateString = "2024-11-27T15:13:51.429Z";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = format.parse(dateString);
        LocalDateTime localDateTime = date.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime();

        User user = new User(1L, "John Doe", "john@example.com", "john123", "password", localDateTime);

        UserDTO userDTO = userMapper.toDTO(user);

        assertNotNull(userDTO);
        assertEquals(1L, userDTO.getId());
        assertEquals("John Doe", userDTO.getName());
        assertEquals("john@example.com", userDTO.getEmail());
        assertEquals("john123", userDTO.getLogin());
        assertEquals("password", userDTO.getPassword());

        LocalDate expectedDate = date.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDate();

        Date dateFromDTO = userDTO.getDate();
        LocalDate actualDate = dateFromDTO.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDate();

        assertEquals(expectedDate, actualDate, "A data não coincide!");
    }


    @Test
    void testToAccountDTO_withValidAccount() {
        Account account = new Account(1L, AccountType.CORRENTE, 1000.0, "0000-1", true);

        CurrencyBalance mockBalance = new CurrencyBalance(1000.0, 200.0, 150.0, 500.0);
        when(currencyConversionService.convertToCurrencyBalance(1000.0, "BRL")).thenReturn(mockBalance);

        AccountDTO accountDTO = userMapper.toAccountDTO(account);

        assertNotNull(accountDTO);
        assertEquals(1L, accountDTO.getId());
        assertEquals(AccountType.CORRENTE, accountDTO.getType());
        assertEquals(1000.0, accountDTO.getBalance());
        assertEquals("0000-1", accountDTO.getNumber());
        assertNotNull(accountDTO.getCurrencyBalance());
    }

    @Test
    void testToAccountDTO_withNullAccount() {
        assertNull(userMapper.toAccountDTO(null));
    }

    @Test
    void testToTransactionDTO_withValidTransaction() {
        LocalDate localDate = LocalDate.now();
        Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        LocalDateTime localDateTime = date.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime();

        Account account = new Account();

        Transaction transaction = new Transaction(TransactionType.DEPOSITO, TransactionCategory.OUTROS, 2000.0, localDateTime, account);

        TransactionDTO transactionDTO = userMapper.toTransactionDTO(transaction);

        assertNotNull(transactionDTO);
        assertEquals(TransactionType.DEPOSITO, transactionDTO.getType());
        assertEquals(TransactionCategory.OUTROS, transactionDTO.getCategory());
        assertEquals(2000.0, transactionDTO.getAmount());

        assertEquals(localDate, localDateTime.toLocalDate(), "As datas não coincidem!");
    }

    @Test
    void testToTransactionDTO_withNullTransaction() {
        assertNull(userMapper.toTransactionDTO(null));
    }

    @Test
    void testUpdateEntityFromDTO_withValidDTO() {
        LocalDate localDate = LocalDate.now();
        Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        LocalDateTime localDateTime = date.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime();
        User user = new User(1L, "Old Name", "old@example.com", "oldLogin", "oldPassword", localDateTime);
        UserDTO userDTO = new UserDTO(1L, "New Name", "new@example.com", "newLogin", "newPassword", date, null);

        userMapper.updateEntityFromDTO(userDTO, user);

        assertEquals("New Name", user.getName());
        assertEquals("new@example.com", user.getEmail());
        assertEquals("newLogin", user.getLogin());
        assertEquals("newPassword", user.getPassword());
    }

    @Test
    void testUpdateEntityFromDTO_withNullDTO() {
        UserDTO userDTO = new UserDTO();

        LocalDate localDate = LocalDate.now();
        Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        LocalDateTime localDateTime = date.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime();
        User user = new User(1L, "Old Name", "old@example.com", "oldLogin", "oldPassword", localDateTime);

        userMapper.updateEntityFromDTO(userDTO, user);

        assertEquals("Old Name", user.getName());
        assertEquals("old@example.com", user.getEmail());
        assertEquals("oldLogin", user.getLogin());
        assertEquals("oldPassword", user.getPassword());
        assertEquals(localDateTime.toLocalDate(), user.getDate().toLocalDate());
    }

    @Test
    void testToAccountDTO_withTransactions() {
        Account account = new Account(1L, AccountType.CORRENTE, 1000.0, "0000-1", true);
        LocalDateTime now = new Date().toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime();
        Transaction transaction1 = new Transaction(TransactionType.DEPOSITO, TransactionCategory.OUTROS, 500.0, now, account);
        Transaction transaction2 = new Transaction(TransactionType.SAQUE, TransactionCategory.TRANSPORTE, 300.0, now, account);
        account.setTransactions(List.of(transaction1, transaction2));
        CurrencyBalance mockBalance = new CurrencyBalance(1000.0, 200.0, 150.0, 500.0);
        when(currencyConversionService.convertToCurrencyBalance(1000.0, "BRL")).thenReturn(mockBalance);
        AccountDTO accountDTO = userMapper.toAccountDTO(account);

        assertNotNull(accountDTO);
        assertEquals(1L, accountDTO.getId());
        assertEquals(AccountType.CORRENTE, accountDTO.getType());
        assertEquals(1000.0, accountDTO.getBalance());
        assertEquals("0000-1", accountDTO.getNumber());
        assertNotNull(accountDTO.getCurrencyBalance());

        assertNotNull(accountDTO.getTransactions());
        assertEquals(2, accountDTO.getTransactions().size());
        assertEquals(TransactionType.DEPOSITO, accountDTO.getTransactions().get(0).getType());
        assertEquals(TransactionCategory.OUTROS, accountDTO.getTransactions().get(0).getCategory());
        assertEquals(500.0, accountDTO.getTransactions().get(0).getAmount());
        assertEquals(TransactionType.SAQUE, accountDTO.getTransactions().get(1).getType());
        assertEquals(TransactionCategory.TRANSPORTE, accountDTO.getTransactions().get(1).getCategory());
        assertEquals(300.0, accountDTO.getTransactions().get(1).getAmount());
    }


    @Test
    void testToDTO_withNullUser() {
        assertNull(userMapper.toDTO(null), "A conversão de um usuário nulo deve retornar null");
    }


}
