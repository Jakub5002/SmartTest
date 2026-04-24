package com.example.backend.dto;

import java.util.List;
import java.util.UUID;

public record QuestionResponse(
        UUID id,
        String content,
        List<String> options
) {}
