package com.nttdata.dto;

import com.nttdata.domain.entity.User;

import java.util.Date;
import java.util.List;

public class UserDTO {
    private Long id;
    private String name;
    private String email;

    private String login;
    private String password;
    private Date date;

    private List<AccountDTO> accounts;

    public UserDTO() {}

    public UserDTO(Long id, String name, String email, String login, String password, Date date, List<AccountDTO> accounts) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.login = login;
        this.password = password;
        this.date = date;
        this.accounts = accounts;
    }

    public UserDTO(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
    public List<AccountDTO> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<AccountDTO> accounts) {
        this.accounts = accounts;
    }

}
