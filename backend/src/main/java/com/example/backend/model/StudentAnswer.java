package com.example.backend.model;

import jakarta.persistence.*; //JPA - Java Persistence API | do operowania na bazie danych

import java.util.UUID; // biblioteka do generowania idnetyfikatorów
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "student_answers")
@Getter @Setter
@NoArgsConstructor
public class StudentAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "result_id")
    private Result result;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;

    private String selectedOption; // np. "B"
}