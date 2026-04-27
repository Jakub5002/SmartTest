package com.example.backend.dto;

import java.util.List;

public record ExamSubmission(List<UserAnswer> answers, java.util.UUID userId) {}
