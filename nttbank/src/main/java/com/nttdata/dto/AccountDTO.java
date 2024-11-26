package com.nttdata.dto;

import com.nttdata.domain.enums.AccountType;

import java.util.List;

public class AccountDTO {

    private Long id;
    private AccountType type;
    private Double balance;
    private Long userId;

    private List<TransactionDTO> transactions;

    public AccountDTO() {}

    public AccountDTO(Long id, AccountType type, Double balance, Long userId, List<TransactionDTO> transactions) {
        this.id = id;
        this.type = type;
        this.balance = balance;
        this.userId = userId;
        this.transactions = transactions;
    }
    public AccountType getType() {
        return type;
    }

    public void setType(AccountType type) {
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<TransactionDTO> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<TransactionDTO> transactions) {
        this.transactions = transactions;
    }
}
