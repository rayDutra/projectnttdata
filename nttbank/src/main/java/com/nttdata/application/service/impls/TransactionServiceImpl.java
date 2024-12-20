package com.nttdata.application.service.impls;

import com.nttdata.domain.entity.Transaction;
import com.nttdata.application.dto.TransactionDTO;

import java.util.List;

public interface TransactionServiceImpl {

    public void delete(Long id);

    public Transaction processTransaction(Transaction transaction);

    public Transaction findById(Long id);

    public Transaction save(Transaction transaction);

    public Transaction update(Long id, TransactionDTO transactionDTO);

    List<Transaction> findAll();

}
