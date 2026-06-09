package com.example.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ExamResult(
        @JsonProperty("score") int score,
        @JsonProperty("totalScore") int totalScore,
        @JsonProperty("percentage") double percentage
) {}