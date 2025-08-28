package com.entertainment.movie_management_system.auth.dto;

public class SigninResponse {

    private String token;
    private String firstName;
    private String role; 

    public SigninResponse() {
    }

    public SigninResponse(String token, String firstName, String role) {
        this.token = token;
        this.firstName = firstName;
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
    
}
