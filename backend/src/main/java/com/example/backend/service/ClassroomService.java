package com.example.backend.service;

import com.example.backend.model.Classroom;
import com.example.backend.model.Exam;
import com.example.backend.model.User;
import com.example.backend.repository.ClassroomRepository;
import com.example.backend.repository.ExamRepository;
import com.example.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClassroomService {
    private final ClassroomRepository classroomRepository;
    private final UserRepository userRepository;
    private final ExamRepository examRepository;

    public Classroom createClassroom(String name){
        Classroom classroom = new Classroom();
        classroom.setName(name);
        return classroomRepository.save(classroom);
    }

    @Transactional
    public void addStudentToClass(UUID classroomId, UUID userId) {
        Classroom classroom = classroomRepository.findById(classroomId)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono klasy o ID: " + classroomId));

        User student = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono użytkownika o ID: " + userId));

        if (!"ROLE_STUDENT".equals(student.getRole())) {
            throw new RuntimeException("Tylko użytkownicy z rolą STUDENT mogą być dodani do klasy!");
        }

        classroom.getStudents().add(student);
        classroomRepository.save(classroom);
    }

    @Transactional
    public void addExamToClass(UUID classroomId, UUID examId){
        Classroom classroom = classroomRepository.findById(classroomId).orElseThrow(() -> new RuntimeException("Nie znaleziono klasy o ID: " + classroomId));
        Exam exam = examRepository.findById(examId).orElseThrow(() -> new RuntimeException("Nie znaleziono egzaminu o ID: " + examId));
        classroom.getExams().add(exam);
        classroomRepository.save(classroom);
    }

    @Transactional
    public void addStudentToClassByEmail(UUID classId, String email) {
        Classroom classroom = classroomRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono klasy o podanym ID"));

        User student = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono studenta o e-mailu: " + email));

        if (!"ROLE_STUDENT".equals(student.getRole())) {
            throw new RuntimeException("Podany użytkownik nie jest studentem!");
        }

        classroom.getStudents().add(student);
        classroomRepository.save(classroom);
    }

    public List<Classroom> getAllClassrooms(){
        return classroomRepository.findAll();
    }
}
