package com.example.backend.model;
import jakarta.persistence.*; //JPA - Java Persistence API | do operowania na bazie danych
import java.util.UUID; // biblioteka do generowania idnetyfikatorów
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "exams")
@Getter @Setter
public class Exam {
    @Id //pole jako klucz główny (Primary Key)
    @GeneratedValue(strategy = GenerationType.UUID) //Instruuje bazę danych, aby automatycznie generował identyfikator
    private UUID _id;

    @Column(nullable = false, unique = true)
    private String _title;

    @Column(nullable = false)
    private String _duration_minutes;

    private UUID _created_by;

    @Column(name = "created_at")
    private boolean _is_active;

}
