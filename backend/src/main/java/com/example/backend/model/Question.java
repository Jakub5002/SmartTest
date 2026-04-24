package com.example.backend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;
import com.example.backend.converters.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "questions")
@Getter @Setter
@NoArgsConstructor
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String content;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<String> options;

    @Column(name = "correct_option")
    private String correct_option;
    @ManyToOne
    @JoinColumn(name = "exam_id", nullable = false)
    @JsonBackReference // Zapobiega serializacji egzaminu wewnątrz pytania
    private Exam exam;


}