package com.example.backend.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*; //JPA - Java Persistence API | do operowania na bazie danych

import java.util.List;
import java.util.UUID; // biblioteka do generowania idnetyfikatorów
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

    @Column(name = "active")
    private Boolean active = true;

    @Column(name = "duration_minutes", nullable = false)
    private int durationMinutes;

    // RELACJA Z USEREM (Adminem)
    @ManyToOne
    @JoinColumn(name = "created_by")
    @JsonIgnore
    private User createdBy;
    // RELACJA Z PYTANIAMI
    @OneToMany(mappedBy = "exam", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<Question> questions;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean deleted = false; //Sprawdzanie czy egzamin jest usunięty

    @OneToMany(mappedBy = "exam", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonIgnore
    private List<ExamSession> sessions;

}