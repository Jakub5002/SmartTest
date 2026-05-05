package com.example.backend.repository;

import com.example.backend.model.Classroom;
import com.example.backend.model.Exam;
import com.example.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClassroomRepository extends JpaRepository<Classroom, UUID> {
    Optional<Classroom> findById(UUID id);
    List<Classroom> findAllByExamsContaining(Exam exam);
    List<Classroom> findAllByStudentsContaining(User student);
}