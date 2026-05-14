package com.example.backend.dto;

import java.util.UUID;

public record ExamDTO(
        UUID id,
        String title,
        Integer durationMinutes,
        Boolean isActive,
        String classroomName
) {}
