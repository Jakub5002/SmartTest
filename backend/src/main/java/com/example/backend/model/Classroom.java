package com.example.backend.model;

import jakarta.persistence.*; // Ważne, żeby importy były z jakarta
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "classrooms")
@Getter @Setter
public class Classroom {
    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    private String name;

    @ManyToMany
    @JoinTable(
            name = "classroom_students",
            joinColumns = @JoinColumn(name = "classroom_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @JsonIgnore
    private Set<User> students = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "classroom_exams",
            joinColumns = @JoinColumn(name = "classroom_id"),
            inverseJoinColumns = @JoinColumn(name = "exam_id")
    )
    @JsonIgnore
    private Set<Exam> exams = new HashSet<>();



}