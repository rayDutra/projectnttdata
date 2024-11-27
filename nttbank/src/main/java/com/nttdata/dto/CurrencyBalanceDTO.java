package com.nttdata.dto;

public class CurrencyBalanceDTO {

    private Double balanceReal;
    private Double balanceDolar;
    private Double balanceEuro;
    private Double balanceIenes;

    public CurrencyBalanceDTO(Double balanceReal, Double balanceDolar, Double balanceEuro, Double balanceIenes) {
        this.balanceReal = balanceReal;
        this.balanceDolar = balanceDolar;
        this.balanceEuro = balanceEuro;
        this.balanceIenes = balanceIenes;
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
