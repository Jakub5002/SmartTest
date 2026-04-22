package com.example.backend.model;
import jakarta.persistence.*; //JPA - Java Persistence API | do operowania na bazie danych

import java.util.List;
import java.util.UUID; // biblioteka do generowania idnetyfikatorów
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "questions")
@Getter @Setter
@NoArgsConstructor
public class Question {
    @Id //pole jako klucz główny (Primary Key)
    @GeneratedValue(strategy = GenerationType.UUID) //Instruuje bazę danych, aby automatycznie generował identyfikator
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "exam_id")
    private Exam exam;

    private String content;

    @ElementCollection
    @CollectionTable(name = "question_options", joinColumns = @JoinColumn(name = "question_id"))
    @Column(name = "option_text")
    private List<String> options;

    private String correctOption;
}
