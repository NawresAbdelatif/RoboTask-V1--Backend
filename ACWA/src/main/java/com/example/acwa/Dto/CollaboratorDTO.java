package com.example.acwa.Dto;

import java.util.Set;

public class CollaboratorDTO {
    private String username;
    private String email;
    private Set<String> roles;

    public CollaboratorDTO() {}

    public CollaboratorDTO(String username, String email, Set<String> roles) {
        this.username = username;
        this.email = email;
        this.roles = roles;
    }

    // GETTERS / SETTERS
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Set<String> getRoles() { return roles; }
    public void setRoles(Set<String> roles) { this.roles = roles; }
}
