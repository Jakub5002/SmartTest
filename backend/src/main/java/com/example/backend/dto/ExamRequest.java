package com.example.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record ExamRequest(
        @NotBlank(message = "Tytuł egzaminu nie może być pusty")
        String title,

        @NotNull(message = "Czas trwania jest wymagany")
        @Min(value = 1, message = "Czas trwania musi być większy niż 0")
        Integer durationMinutes,

        boolean isActive,

        UUID createdBy
) {}