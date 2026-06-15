package com.example.backend.service;

import com.example.backend.model.Classroom;
import com.example.backend.model.Exam;
import com.example.backend.model.User;
import com.example.backend.repository.ClassroomRepository;
import com.example.backend.repository.ExamRepository;
import com.example.backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClassroomServiceTest {

    @Mock
    private ClassroomRepository classroomRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ExamRepository examRepository;

    @InjectMocks
    private ClassroomService classroomService;

    @Test
    void shouldCreateClassroom() {
        String name = "Grupa Lab 1";
        Classroom classroom = new Classroom();
        classroom.setName(name);

        when(classroomRepository.save(any(Classroom.class))).thenReturn(classroom);

        Classroom result = classroomService.createClassroom(name);

        assertNotNull(result);
        assertEquals(name, result.getName());
        verify(classroomRepository, times(1)).save(any(Classroom.class));
    }

    @Test
    void shouldAddStudentToClassByEmail() {
        UUID classId = UUID.randomUUID();
        String email = "student@test.com";

        Classroom classroom = new Classroom();
        classroom.setId(classId);

        User student = new User();
        student.setEmail(email);
        student.setRole("ROLE_STUDENT");

        when(classroomRepository.findById(classId)).thenReturn(Optional.of(classroom));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(student));

        classroomService.addStudentToClassByEmail(classId, email);

        assertTrue(classroom.getStudents().contains(student));
        verify(classroomRepository, times(1)).save(classroom);
    }

    @Test
    void shouldAddExamToClass() {
        UUID classId = UUID.randomUUID();
        UUID examId = UUID.randomUUID();

        Classroom classroom = new Classroom();
        classroom.setId(classId);

        Exam exam = new Exam();
        exam.setId(examId);

        when(classroomRepository.findById(classId)).thenReturn(Optional.of(classroom));
        when(examRepository.findById(examId)).thenReturn(Optional.of(exam));

        classroomService.addExamToClass(classId, examId);

        assertTrue(classroom.getExams().contains(exam));
        verify(classroomRepository, times(1)).save(classroom);
    }

    @Test
    void shouldThrowExceptionWhenClassNotFound() {
        UUID classId = UUID.randomUUID();
        when(classroomRepository.findById(classId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            classroomService.addStudentToClassByEmail(classId, "test@test.com");
        });

        assertNotNull(exception);
    }
}