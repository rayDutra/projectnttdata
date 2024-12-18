package com.nttdata.application.dto;

import com.nttdata.domain.enums.AccountType;

import java.util.List;

public class AccountDTO {

    private Long id;
    private AccountType type;
    private Double balance;
    private String number;
    private Long userId;
    private List<TransactionDTO> transactions;
    private CurrencyBalanceDTO currencyBalance;

    public AccountDTO() {}

    public AccountDTO(Long id, AccountType type, Double balance, String number, Long userId, List<TransactionDTO> transactions, CurrencyBalanceDTO currencyBalance) {
        this.id = id;
        this.type = type;
        this.balance = balance;
        this.number = number;
        this.userId = userId;
        this.transactions = transactions;
        this.currencyBalance = currencyBalance;
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

    public CurrencyBalanceDTO getCurrencyBalance() {
        return currencyBalance;
    }

    public void setCurrencyBalance(CurrencyBalanceDTO currencyBalance) {
        this.currencyBalance = currencyBalance;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
