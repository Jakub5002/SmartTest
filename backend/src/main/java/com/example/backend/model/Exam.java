package com.example.backend.model;
import jakarta.persistence.*; //JPA - Java Persistence API | do operowania na bazie danych
import java.util.UUID; // biblioteka do generowania idnetyfikatorów
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "exams")
@Getter @Setter
@NoArgsConstructor
public class Exam {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String title;

    @Column(nullable = false)
    private int duration_minutes;

    private boolean is_active;

    @ManyToOne // Wiele egzaminów może być stworzonych przez jednego admina
    @JoinColumn(name = "created_by")
    private User createdBy;
}
