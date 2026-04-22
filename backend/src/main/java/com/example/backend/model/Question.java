package com.example.backend.model;
import jakarta.persistence.*; //JPA - Java Persistence API | do operowania na bazie danych
import java.util.UUID; // biblioteka do generowania idnetyfikatorów
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "questions")
@Getter @Setter
public class Question {
    @Id //pole jako klucz główny (Primary Key)
    @GeneratedValue(strategy = GenerationType.UUID) //Instruuje bazę danych, aby automatycznie generował identyfikator
    private UUID _id;


    private UUID _exam_id;

}
