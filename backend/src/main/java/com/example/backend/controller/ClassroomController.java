package com.example.backend.controller;

import com.example.backend.model.Classroom;
import com.example.backend.service.ClassroomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/classrooms")
@RequiredArgsConstructor
public class ClassroomController {
    private final ClassroomService classroomService;

    @GetMapping
    public ResponseEntity<List<Classroom>> getAllClassrooms() {
        return ResponseEntity.ok(classroomService.getAllClassrooms());
    }



    @PostMapping
    public ResponseEntity<Classroom> create(@RequestBody String name){
        return ResponseEntity.ok(classroomService.createClassroom(name));
    }

    @PostMapping("/{classId}/students")
    public ResponseEntity<String> addStudent(@PathVariable UUID classId, @RequestParam String email) {
        classroomService.addStudentToClassByEmail(classId, email);
        return ResponseEntity.ok("Student został dodany do klasy.");
    }

    @PostMapping("/{classId}/exams/{examId}")
    public ResponseEntity<String> addExam(@PathVariable UUID classId, @PathVariable UUID examId){
        classroomService.addExamToClass(classId, examId);
        return ResponseEntity.ok("Egzamin został przypisany do klasy.");
    }

}
