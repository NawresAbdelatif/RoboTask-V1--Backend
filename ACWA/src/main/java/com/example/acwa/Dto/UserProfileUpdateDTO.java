package com.example.acwa.Dto;

public class UserProfileUpdateDTO {
    private String username;
    private String email;

    public UserProfileUpdateDTO() {}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
