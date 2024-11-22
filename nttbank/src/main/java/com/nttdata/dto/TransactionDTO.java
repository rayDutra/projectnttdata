package com.nttdata.dto;

import com.nttdata.domain.entity.Transaction;

import java.util.Date;

public class TransactionDTO {

    private Long id;
    private String type;
    private Double amount;
    private Date date;
    private Long accountId;

    public TransactionDTO() {}

    public TransactionDTO(Long id, String type, Double amount, Date date, Long accountId) {
        this.id = id;
        this.type = type;
        this.amount = amount;
        this.date = date;
        this.accountId = accountId;
    }

    public TransactionDTO(Transaction transaction) {
        this.id = transaction.getId();
        this.type = transaction.getType().name();
        this.amount = transaction.getAmount();
        this.date = transaction.getDate();
        this.accountId = transaction.getAccount().getId();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
