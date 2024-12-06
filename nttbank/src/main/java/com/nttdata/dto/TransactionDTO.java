package com.nttdata.dto;

import com.nttdata.domain.entity.Transaction;
import com.nttdata.domain.enums.TransactionCategory;
import com.nttdata.domain.enums.TransactionType;

import java.util.Date;

public class TransactionDTO {

    private Long id;
    private TransactionType type;
    private TransactionCategory category;
    private Double amount;
    private Date date;
    private Long accountId;

    public TransactionDTO() {}

    public TransactionDTO(Long id, TransactionType type, TransactionCategory category, Double amount, Date date, Long accountId) {
        this.id = id;
        this.type = type;
        this.category = category;
        this.amount = amount;
        this.date = date;
        this.accountId = accountId;
    }

    public TransactionDTO(Transaction transaction) {
        this.id = transaction.getId();
        this.type = transaction.getType();
        this.category = transaction.getCategory();
        this.amount = transaction.getAmount();
        this.accountId = transaction.getAccount().getId();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public TransactionCategory getCategory() {
        return category;
    }

    public void setCategory(TransactionCategory category) {
        this.category = category;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }
}
