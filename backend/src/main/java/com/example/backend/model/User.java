//package model;
package com.example.backend.model;

import jakarta.persistence.*; //JPA - Java Persistence API | do operowania na bazie danych
import lombok.NoArgsConstructor;

import java.util.UUID; // biblioteka do generowania idnetyfikatorów
import java.util.Date; // służy do zapisania czasu w w którym użytkownik został utworzony
import java.util.Objects;

@Entity
@Table(name = "users")
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

    //Settery
    public void setId(UUID id) { this.id = id; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setRole(String role) { this.role = role; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    //Gettery
    public UUID getId() { return id; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public Date getCreatedAt() { return createdAt; }
}
