package com.example.backend.service;

import com.example.backend.model.User;
import com.example.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public User register(String email, String password){
        if(userRepository.existsByEmail(email)){
            throw new RuntimeException("Emaill juz zajety");
        }

        String hashedPassword = passwordEncoder.encode(password);
        User user = new User();
        user.setEmail(email);
        user.setPassword(hashedPassword);
        user.setRole("STUDENT");

        return userRepository.save(user);
    }

    public boolean login(String email, String password){
        Optional<User> userOpt = userRepository.findByEmail(email);

        if(userOpt.isPresent()){
            User user = userOpt.get();
            return passwordEncoder.matches(password, user.getPassword());
        }
        return false;
    }
}
