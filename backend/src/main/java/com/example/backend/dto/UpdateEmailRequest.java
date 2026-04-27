package com.example.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class UpdateEmailRequest {
    @NotBlank(message = "Emial nie moze byc pusty !")
    @Email
    private String newEmail;
}
