package com.example.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UpdatePasswordRequest {
    @NotBlank
    private String oldPassword;
    @NotBlank
    @Size(min = 6, message = "Haslo musi miec co najmniej 6 znakow")
    private String newPassword;
}
