package com.example.backend.dto;

import java.util.UUID;

public record UserAnswer(UUID questionId, String selectedOption) {}