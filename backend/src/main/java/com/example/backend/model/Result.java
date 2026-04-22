package com.example.backend.model;

import jakarta.persistence.*; //JPA - Java Persistence API | do operowania na bazie danych

import java.time.LocalDateTime;
import java.util.UUID; // biblioteka do generowania idnetyfikatorów
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "results")
@Getter @Setter
@NoArgsConstructor
public class Result {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "exam_id")
    private Exam exam;

    private int score;

    private LocalDateTime startedAt = LocalDateTime.now();
    private LocalDateTime finishedAt;
}
