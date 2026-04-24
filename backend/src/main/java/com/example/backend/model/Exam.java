package com.example.backend.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*; //JPA - Java Persistence API | do operowania na bazie danych

import java.util.List;
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

    // RELACJA Z USEREM (Adminem)
    @ManyToOne
    @JoinColumn(name = "created_by") // To stworzy kolumnę created_by w tabeli exams
    private User createdBy;

    // RELACJA Z PYTANIAMI
    @OneToMany(mappedBy = "exam")
    @JsonIgnore
    private List<Question> questions;
}