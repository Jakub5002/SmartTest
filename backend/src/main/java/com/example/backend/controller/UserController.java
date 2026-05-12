package com.example.backend.controller;

import com.example.backend.dto.UpdateEmailRequest;
import com.example.backend.dto.UpdatePasswordRequest;
import com.example.backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    @PutMapping("/update-email")
    public ResponseEntity<?> updateEamil(@Valid @RequestBody UpdateEmailRequest request, Principal principal){
        userService.updateEmail(principal.getName(), request.getNewEmail());
        return  ResponseEntity.ok("Email został pomyslnie zaktualizowany");
    }

    @PutMapping("/update-password")
    public ResponseEntity<?> updateEmail(@Valid @RequestBody UpdatePasswordRequest request, Principal principal){
        userService.updatePassword(principal.getName(), request.getOldPassword(), request.getNewPassword());
        return  ResponseEntity.ok("Haslo zostalo zmienione");
    }
}
