package com.nttdata.domain.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "currency_balances")
public class CurrencyBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    private Double balanceReal;
    private Double balanceDolar;
    private Double balanceEuro;
    private Double balanceIenes;

    public CurrencyBalance() {}

    public CurrencyBalance(Double balanceReal, Double balanceDolar, Double balanceEuro, Double balanceIenes) {
        this.balanceReal = balanceReal;
        this.balanceDolar = balanceDolar;
        this.balanceEuro = balanceEuro;
        this.balanceIenes = balanceIenes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Double getBalanceReal() {
        return balanceReal;
    }

    public void setBalanceReal(Double balanceReal) {
        this.balanceReal = balanceReal;
    }

    public Double getBalanceDolar() {
        return balanceDolar;
    }

    public void setBalanceDolar(Double balanceDolar) {
        this.balanceDolar = balanceDolar;
    }

    public Double getBalanceEuro() {
        return balanceEuro;
    }

    public void setBalanceEuro(Double balanceEuro) {
        this.balanceEuro = balanceEuro;
    }

    public Double getBalanceIenes() {
        return balanceIenes;
    }

    public void setBalanceIenes(Double balanceIenes) {
        this.balanceIenes = balanceIenes;
    }
}
