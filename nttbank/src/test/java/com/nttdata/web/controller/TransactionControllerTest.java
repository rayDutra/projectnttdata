package com.nttdata.web.controller;

import com.nttdata.infrastructure.mapper.TransactionMapper;
import com.nttdata.application.service.AccountService;
import com.nttdata.application.service.impls.TransactionServiceImpl;
import com.nttdata.domain.entity.Account;
import com.nttdata.domain.entity.Transaction;
import com.nttdata.domain.enums.TransactionCategory;
import com.nttdata.domain.enums.TransactionType;
import com.nttdata.application.dto.TransactionDTO;
import com.nttdata.infrastructure.repository.TransactionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TransactionControllerTest {

    @Mock
    private TransactionServiceImpl transactionServiceImpl;

    @Mock
    private AccountService accountService;

    @Mock
    private TransactionMapper transactionMapper;

    @InjectMocks
    private TransactionController transactionController;
    @Mock
    private TransactionRepository transactionRepository;


    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(transactionController).build();
    }

    @Test
    void testCreateTransaction_Success() throws Exception {
        TransactionDTO transactionDTO = new TransactionDTO(1L, TransactionType.PIX, TransactionCategory.LAZER, 100.0, new Date(), 1L);
        Account account = new Account();
        account.setId(1L);

        Transaction transaction = new Transaction();
        Transaction processedTransaction = new Transaction();

        when(accountService.findById(1L)).thenReturn(account);
        when(transactionMapper.toEntity(transactionDTO, account)).thenReturn(transaction);
        when(transactionServiceImpl.processTransaction(transaction)).thenReturn(processedTransaction);
        when(transactionMapper.toDTO(processedTransaction)).thenReturn(transactionDTO);

        MvcResult result = mockMvc.perform(post("/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"type\":\"PIX\",\"category\":\"LAZER\",\"amount\":100.0,\"accountId\":1}"))
            .andExpect(status().isCreated())
            .andReturn();

        System.out.println(result.getResponse().getContentAsString());


    }

    @Test
    void testGetAllTransactions_Success() throws Exception {
        Transaction transaction1 = new Transaction();
        Transaction transaction2 = new Transaction();
        TransactionDTO transactionDTO1 = new TransactionDTO(1L, TransactionType.PIX, TransactionCategory.OUTROS, 100.0, new Date(), 1L);
        TransactionDTO transactionDTO2 = new TransactionDTO(2L, TransactionType.SAQUE, TransactionCategory.TRANSPORTE, 500.0, new Date(), 1L);

        when(transactionServiceImpl.findAll()).thenReturn(List.of(transaction1, transaction2));
        when(transactionMapper.toDTO(transaction1)).thenReturn(transactionDTO1);
        when(transactionMapper.toDTO(transaction2)).thenReturn(transactionDTO2);

        mockMvc.perform(get("/transactions"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].type").value("PIX"))
            .andExpect(jsonPath("$[1].type").value("SAQUE"))
            .andExpect(jsonPath("$[0].amount").value(100.0))
            .andExpect(jsonPath("$[1].amount").value(500.0));
    }

    @Test
    void testGetTransactionById_Success() throws Exception {
        Transaction transaction = new Transaction();
        TransactionDTO transactionDTO = new TransactionDTO(1L, TransactionType.BOLETO, TransactionCategory.OUTROS, 100.0, new Date(), 1L);

        when(transactionServiceImpl.findById(1L)).thenReturn(transaction);
        when(transactionMapper.toDTO(transaction)).thenReturn(transactionDTO);

        mockMvc.perform(get("/transactions/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.type").value("BOLETO"))
            .andExpect(jsonPath("$.amount").value(100.0));
    }

    @Test
    void testUpdateTransaction_Success() throws Exception {
        TransactionDTO transactionDTO = new TransactionDTO(1L, TransactionType.BOLETO, TransactionCategory.LAZER, 150.0, new Date(), 1L);
        Transaction transaction = new Transaction();

        when(transactionServiceImpl.update(1L, transactionDTO)).thenReturn(transaction);
        when(transactionMapper.toDTO(transaction)).thenReturn(transactionDTO);

        MvcResult result = mockMvc.perform(put("/transactions/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"type\":\"BOLETO\",\"category\":\"LAZER\",\"amount\":150.0,\"accountId\":1}"))
            .andExpect(status().isOk())
            .andReturn();

        System.out.println("Response Body: " + result.getResponse().getContentAsString());

    }
    @Test
    void testCreateTransaction_AccountNotFound() {
        TransactionDTO transactionDTO = new TransactionDTO(
            null,
            TransactionType.BOLETO,
            TransactionCategory.LAZER,
            100.0,
            new Date(),
            1L
        );

        when(accountService.findById(1L)).thenReturn(null);

        assertThrows(EntityNotFoundException.class, () -> {
            transactionController.createTransaction(transactionDTO);
        });
    }

}
