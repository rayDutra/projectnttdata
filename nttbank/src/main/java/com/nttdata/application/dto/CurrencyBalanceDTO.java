package com.nttdata.application.dto;

public class CurrencyBalanceDTO {

    private String balanceReal;
    private String balanceDolar;
    private String balanceEuro;
    private String balanceIenes;

    public CurrencyBalanceDTO(String balanceReal, String balanceDolar, String balanceEuro, String balanceIenes) {
        this.balanceReal = balanceReal;
        this.balanceDolar = balanceDolar;
        this.balanceEuro = balanceEuro;
        this.balanceIenes = balanceIenes;
    }

    public CurrencyBalanceDTO() {
    }

    public String getBalanceReal() {
        return balanceReal;
    }

    public void setBalanceReal(String balanceReal) {
        this.balanceReal = balanceReal;
    }

    public String getBalanceDolar() {
        return balanceDolar;
    }

    public void setBalanceDolar(String balanceDolar) {
        this.balanceDolar = balanceDolar;
    }

    public String getBalanceEuro() {
        return balanceEuro;
    }

    public void setBalanceEuro(String balanceEuro) {
        this.balanceEuro = balanceEuro;
    }

    public String getBalanceIenes() {
        return balanceIenes;
    }

    public void setBalanceIenes(String balanceIenes) {
        this.balanceIenes = balanceIenes;
    }
}
