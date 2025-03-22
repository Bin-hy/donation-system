package com.example.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String emailTo;
    private String emailCode;
}
