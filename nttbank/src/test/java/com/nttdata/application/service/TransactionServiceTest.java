package com.nttdata.application.service;

import com.nttdata.domain.entity.Account;
import com.nttdata.domain.entity.Transaction;
import com.nttdata.domain.enums.TransactionCategory;
import com.nttdata.domain.enums.TransactionType;
import com.nttdata.application.dto.TransactionDTO;
import com.nttdata.infrastructure.repository.AccountRepository;
import com.nttdata.infrastructure.repository.TransactionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TransactionServiceTest {

    @InjectMocks
    private TransactionService transactionService;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFindAllTransactions() {
        Transaction transaction1 = new Transaction();
        transaction1.setId(1L);
        transaction1.setAmount(100.0);

        Transaction transaction2 = new Transaction();
        transaction2.setId(2L);
        transaction2.setAmount(200.0);

        List<Transaction> mockTransactions = Arrays.asList(transaction1, transaction2);
        when(transactionRepository.findAll()).thenReturn(mockTransactions);

        List<Transaction> transactions = transactionService.findAll();

        assertNotNull(transactions, "A lista de transações não deve ser nula");
        assertEquals(2, transactions.size(), "A lista de transações deve conter 2 elementos");
        assertEquals(100.0, transactions.get(0).getAmount(), "O valor da primeira transação deve ser 100.0");
        assertEquals(200.0, transactions.get(1).getAmount(), "O valor da segunda transação deve ser 200.0");
    }

    @Test
    public void testFindAllTransactions_EmptyList() {
        when(transactionRepository.findAll()).thenReturn(Arrays.asList());

        List<Transaction> transactions = transactionService.findAll();

        assertNotNull(transactions, "A lista de transações não deve ser nula");
        assertTrue(transactions.isEmpty(), "A lista de transações deve estar vazia");
    }

    @Test
    public void testSaveTransaction() {
        Transaction transaction = new Transaction();
        transaction.setAmount(100.0);
        transaction.setType(TransactionType.DEPOSITO);

        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        Transaction savedTransaction = transactionService.save(transaction);

        assertNotNull(savedTransaction, "A transação salva não deve ser nula");
        assertEquals(100.0, savedTransaction.getAmount(), "O valor da transação deve ser 100.0");
    }

    @Test
    public void testFindById() {
        Long transactionId = 1L;
        Transaction transaction = new Transaction();
        transaction.setId(transactionId);
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));

        Transaction foundTransaction = transactionService.findById(transactionId);

        assertNotNull(foundTransaction, "A transação não deve ser nula");
        assertEquals(transactionId, foundTransaction.getId(), "O ID da transação deve ser 1");
    }

    @Test
    public void testFindById_TransactionNotFound() {
        Long transactionId = 999L;
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> transactionService.findById(transactionId),
            "Deveria lançar uma exceção quando a transação não for encontrada");
    }

    @Test
    public void testUpdateTransaction() {
        Long transactionId = 1L;
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setType(TransactionType.SAQUE);
        transactionDTO.setAmount(200.0);
        transactionDTO.setCategory(TransactionCategory.OUTROS);
        transactionDTO.setDate(new Date());

        Transaction existingTransaction = new Transaction();
        existingTransaction.setId(transactionId);
        existingTransaction.setAmount(100.0);

        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(existingTransaction));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(existingTransaction);

        Transaction updatedTransaction = transactionService.update(transactionId, transactionDTO);

        assertNotNull(updatedTransaction, "A transação atualizada não deve ser nula");
        assertEquals(transactionDTO.getAmount(), updatedTransaction.getAmount(), "O valor da transação deve ser atualizado para 200.0");
    }

    @Test
    public void testDeleteTransaction() {
        Long transactionId = 1L;
        Transaction transaction = new Transaction();
        transaction.setId(transactionId);

        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));
        doNothing().when(transactionRepository).delete(transaction);

        transactionService.delete(transactionId);

        verify(transactionRepository, times(1)).delete(transaction);
    }

    @Test
    public void testProcessTransaction_Deposito() {
        Account account = new Account();
        account.setId(1L);
        account.setBalance(1000.0);

        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setAmount(500.0);
        transaction.setType(TransactionType.DEPOSITO);

        when(accountRepository.save(any(Account.class))).thenReturn(account);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        Transaction processedTransaction = transactionService.processTransaction(transaction);

        assertNotNull(processedTransaction, "A transação processada não deve ser nula");
        assertEquals(1500.0, account.getBalance(), "O saldo da conta deve ser atualizado para 1500.0");
    }

    @Test
    public void testProcessTransaction_Saque() {
        Account account = new Account();
        account.setId(1L);
        account.setBalance(1000.0);

        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setAmount(200.0);
        transaction.setType(TransactionType.SAQUE);

        when(accountRepository.save(any(Account.class))).thenReturn(account);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        Transaction processedTransaction = transactionService.processTransaction(transaction);

        assertNotNull(processedTransaction, "A transação processada não deve ser nula");
        assertEquals(795.0, account.getBalance(), "O saldo da conta deve ser atualizado para 795.0 (desconto de 5)");
    }

    @Test
    public void testProcessTransaction_Transferencia_InsufficientBalance() {
        Account account = new Account();
        account.setId(1L);
        account.setBalance(100.0);

        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setAmount(200.0);
        transaction.setType(TransactionType.TRANSFERENCIA);

        assertThrows(IllegalStateException.class, () -> transactionService.processTransaction(transaction),
            "Deveria lançar exceção de saldo insuficiente para transferência com desconto.");
    }

    @Test
    public void testProcessTransaction_InvalidAmount() {
        Account account = new Account();
        account.setId(1L);
        account.setBalance(1000.0);

        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setAmount(-500.0);
        transaction.setType(TransactionType.DEPOSITO);

        assertThrows(IllegalArgumentException.class, () -> transactionService.processTransaction(transaction),
            "Deveria lançar exceção se o valor da transação for menor ou igual a zero.");
    }

    @Test
    public void testProcessTransaction_PIX() {
        Account account = new Account();
        account.setId(1L);
        account.setBalance(1000.0);

        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setAmount(500.0);
        transaction.setType(TransactionType.PIX);

        when(accountRepository.save(any(Account.class))).thenReturn(account);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        Transaction processedTransaction = transactionService.processTransaction(transaction);

        assertNotNull(processedTransaction, "A transação processada não deve ser nula");
        assertEquals(500.0, account.getBalance(), "O saldo da conta deve ser atualizado para 500.0");
    }

    @Test
    public void testProcessTransaction_Boleto() {
        Account account = new Account();
        account.setId(1L);
        account.setBalance(1000.0);

        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setAmount(200.0);
        transaction.setType(TransactionType.BOLETO);

        when(accountRepository.save(any(Account.class))).thenReturn(account);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        Transaction processedTransaction = transactionService.processTransaction(transaction);

        assertNotNull(processedTransaction, "A transação processada não deve ser nula");
        assertEquals(800.0, account.getBalance(), "O saldo da conta deve ser atualizado para 800.0");
    }

    @Test
    public void testProcessTransaction_InvalidTransactionType() {
        Account account = new Account();
        account.setId(1L);
        account.setBalance(1000.0);

        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setAmount(500.0);
        transaction.setType(null);

        assertThrows(NullPointerException.class, () -> transactionService.processTransaction(transaction),
            "Deveria lançar exceção para tipo de transação nulo.");
    }

}
