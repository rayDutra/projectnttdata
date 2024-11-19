package com.nttdata.dto;

import java.util.Date;

public class UserDTO {

    private String name;

    private String email;
    private String password;
    private Date date;

    public UserDTO() {
    }

    public UserDTO(String name, String email, String password, Date date) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.date = date;
    }
    public String getName() {
        return name;
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
}
