//package model;
package com.example.backend.model;

import jakarta.persistence.*; //JPA - Java Persistence API | do operowania na bazie danych
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID; // biblioteka do generowania idnetyfikatorów
import java.util.Date; // służy do zapisania czasu w w którym użytkownik został utworzony
import java.util.Objects;

@Entity
@Table(name = "users")
@Getter @Setter
@NoArgsConstructor
public class User {
    @Id //pole jako klucz główny (Primary Key)
    @GeneratedValue(strategy = GenerationType.UUID) //Instruuje bazę danych, aby automatycznie generował identyfikator
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private String role; //ADMIN lub STUDENT

    @Column(name = "created_at")
    private Date createdAt = new Date();

}
