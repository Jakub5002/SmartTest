package com.example.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AuthRequest {
    @NotBlank(message = "Emial nie moze byc pusty !") //nie null albo czy nie same spacje
    @Email(message = "To nie jest format emaila!") //patrzy czy jest mailem czyli czy np ma @
    private String email;

    @NotBlank(message = "Haslo nie moze byc puste")
    @Size(min = 6, message = "Haslo musi miec co najmniej 6 znakow")
    private String password;

    private boolean admin;
}