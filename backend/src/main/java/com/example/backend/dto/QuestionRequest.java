package com.example.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public record QuestionRequest(
        @NotBlank(message = "Treść pytania nie może być pusta")
        String content,

        @NotBlank(message = "Poprawna odpowiedź nie może być pusta")
        String correctOption,

        @NotEmpty(message = "Opcje nie mogą być puste")
        List<String> options,

        @NotNull(message = "ID egzaminu jest wymagane")
        UUID examId
) {}