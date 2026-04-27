package com.example.backend.service;

import com.example.backend.dto.AuthRequest;
import com.example.backend.model.User;
import com.example.backend.repository.UserRepository;
import com.example.backend.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public User register(AuthRequest request){
        if(userRepository.existsByEmail(request.getEmail())){
            throw new RuntimeException("Emaill juz zajety");
        }

        String hashedPassword = passwordEncoder.encode(request.getPassword());
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(hashedPassword);
        if(request.isAdmin()){
            user.setRole("ROLE_ADMIN");
        } else{
            user.setRole("ROLE_STUDENT");
        }

        return userRepository.save(user);
    }

    public String login(String email, String password){
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Błędny dane"));

        if (passwordEncoder.matches(password, user.getPassword())) {

            List<String> roles = List.of(user.getRole());
            return jwtUtils.generateToken(email, roles);
        }
        throw new RuntimeException("Błędne dane ");
    }
}
