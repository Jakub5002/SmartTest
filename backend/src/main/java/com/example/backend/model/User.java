//package model;
package com.example.backend.model;

import jakarta.persistence.*; //JPA - Java Persistence API | do operowania na bazie danych
import java.util.UUID; // biblioteka do generowania idnetyfikatorów
import java.util.Date; // służy do zapisania czasu w w którym użytkownik został utworzony
import java.util.Objects;

@Entity
@Table(name = "users")
public class User {
    @Id //pole jako klucz główny (Primary Key)
    @GeneratedValue(strategy = GenerationType.UUID) //Instruuje bazę danych, aby automatycznie generował identyfikator
    private UUID _id;

    @Column(nullable = false, unique = true)
    private String _email;

    @Column(nullable = false)
    private String _password;

    private String _role; //ADMIN lub STUDENT

    @Column(name = "created_at")
    private Date _createdAt = new Date();

    //Settery
    public void setId(UUID id) { this._id = id; }
    public void setEmail(String email) { this._email = email; }
    public void setPassword(String password) { this._password = password; }
    public void setRole(String role) { this._role = role; }
    public void setCreatedAt(Date createdAt) { this._createdAt = createdAt; }

    //Gettery
    public UUID getId() { return _id; }
    public String getEmail() { return _email; }
    public String getPassword() { return _password; }
    public String getRole() { return _role; }
    public Date getCreatedAt() { return _createdAt; }
}
