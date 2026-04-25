package com.example.backend.controller;

import com.example.backend.dto.AuthRequest;
import com.example.backend.model.User;
import com.example.backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    public final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthRequest request){
        try{
            User registeredUser = authService.register(request.getEmail(), request.getPassword());
            return ResponseEntity.ok("Użytkownik zarejestowant ok! " +  registeredUser.getId());
        }catch(RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request){
        String token = authService.login(request.getEmail(), request.getPassword());

        if(token != null){
            return ResponseEntity.ok(Map.of("token", token));
        }else{
            return ResponseEntity.status(401).body("Błedne dane");
        }
    }
}
