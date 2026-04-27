package com.example.backend.controller;

import com.example.backend.model.Classroom;
import com.example.backend.service.ClassroomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/classrooms")
@RequiredArgsConstructor
public class ClassroomController {
    private final ClassroomService classroomService;

    @PostMapping
    public ResponseEntity<Classroom> create(@RequestBody String name){
        return ResponseEntity.ok(classroomService.createClassroom(name));
    }

    @PostMapping("/{classId}/students/{userId}")
    public ResponseEntity<String> addStudent(@PathVariable UUID classId, @PathVariable UUID userId ){
        classroomService.addStudentToClass(classId, userId);
        return ResponseEntity.ok("Student został dodany do klasy.");
    }

    @PostMapping("/{classId}/exams/{examId}")
    public ResponseEntity<String> addExam(@PathVariable UUID classId, @PathVariable UUID examId){
        classroomService.addExamToClass(classId, examId);
        return ResponseEntity.ok("Egzamin został przypisany do klasy.");
    }
}
