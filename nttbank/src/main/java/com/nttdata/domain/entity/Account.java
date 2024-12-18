package com.nttdata.domain.entity;

import com.nttdata.domain.enums.AccountType;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private AccountType type;
    private Double balance = 0.0;

    private String number;

    private boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "account")
    private List<Transaction> transactions;

    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private CurrencyBalance currencyBalance;

    public Account() {
    }

    public Account(Long id, AccountType type, Double balance, String number, boolean active, User user, List<Transaction> transactions, CurrencyBalance currencyBalance) {
        this.id = id;
        this.type = type;
        this.balance = balance;
        this.number = number;
        this.active = active;
        this.user = user;
        this.transactions = transactions;
        this.currencyBalance = currencyBalance;
    }

    public Account(Long id, AccountType type, Double balance, String number,  boolean active) {
        this.id = id;
        this.type = type;
        this.balance = balance;
        this.number = number;
        this.active = active;
    }

    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }

    public AccountType getType() {
        return type;
    }

    public void setType(AccountType type) {
        this.type = type;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public CurrencyBalance getCurrencyBalance() {
        return currencyBalance;
    }

    public void setCurrencyBalance(CurrencyBalance currencyBalance) {
        this.currencyBalance = currencyBalance;
    }

    public void updateBalance(Double value) {
        this.balance += value;
    }

    public void deactivate() {
        this.active = false;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
