package com.nttdata.application.mapper;

import com.nttdata.domain.entity.Account;
import com.nttdata.domain.entity.Transaction;
import com.nttdata.domain.enums.TransactionCategory;
import com.nttdata.domain.enums.TransactionType;
import com.nttdata.dto.TransactionDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

class TransactionMapperTest {

    @InjectMocks
    private TransactionMapper transactionMapper;

    private static final String DATE_FORMAT = "yyyy-MM-dd";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testToDTO_withValidTransaction() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = sdf.parse("2024-12-02");
        LocalDateTime localDateTime = date.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime();
        Transaction transaction = new Transaction();
        transaction.setId(1L);
        transaction.setType(TransactionType.DEPOSITO);
        transaction.setCategory(TransactionCategory.LAZER);
        transaction.setAmount(1000.0);
        transaction.setDate(localDateTime);

        Account account = new Account();
        account.setId(123L);
        transaction.setAccount(account);

        TransactionDTO transactionDTO = transactionMapper.toDTO(transaction);

        assertNotNull(transactionDTO);
        assertEquals(1L, transactionDTO.getId());
        assertEquals(TransactionType.DEPOSITO, transactionDTO.getType());
        assertEquals(TransactionCategory.LAZER, transactionDTO.getCategory());
        assertEquals(1000.0, transactionDTO.getAmount());

        assertEquals(sdf.parse("2024-12-02"), transactionDTO.getDate());
        assertEquals(123L, transactionDTO.getAccountId());
    }


    @Test
    void testToDTO_withNullTransaction_returnsNull() {
        TransactionDTO transactionDTO = transactionMapper.toDTO(null);
        assertNull(transactionDTO);
    }

    @Test
    void testToEntity_withValidTransactionDTO() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        Date date = sdf.parse("2024-12-02");

        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setId(null);
        transactionDTO.setType(TransactionType.BOLETO);
        transactionDTO.setCategory(TransactionCategory.SAUDE);
        transactionDTO.setAmount(500.0);
        transactionDTO.setDate(date);

        Account account = new Account();
        account.setId(123L);

        Transaction transaction = transactionMapper.toEntity(transactionDTO, account);

        assertNotNull(transaction);
        assertEquals(null, transaction.getId());
        assertEquals(TransactionType.BOLETO, transaction.getType());
        assertEquals(TransactionCategory.SAUDE, transaction.getCategory());
        assertEquals(500.0, transaction.getAmount());

        Date transactionDate = Date.from(transaction.getDate().atZone(ZoneId.systemDefault()).toInstant());
        assertEquals(sdf.format(date), sdf.format(transactionDate));

        assertNotNull(transaction.getAccount());
        assertEquals(123L, transaction.getAccount().getId());
    }

    @Test
    void testToEntity_withNullTransactionDTO_throwsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionMapper.toEntity(null, null);
        });
        assertEquals("TransactionDTO e Account não podem ser nulos", exception.getMessage());
    }

}
