package com.nttdata.dto;
public class AccountDTO {

    private Long id;
    private String type;
    private Double balance;
    private Long userId;

    public AccountDTO() {}

    public AccountDTO(Long id, String type, Double balance, Long userId) {
        this.id = id;
        this.type = type;
        this.balance = balance;
        this.userId = userId;
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
}
