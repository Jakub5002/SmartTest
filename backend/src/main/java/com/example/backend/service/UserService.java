package com.example.backend.service;

import com.example.backend.model.User;
import com.example.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    public final BCryptPasswordEncoder passwordEncoder;

    public void updateEmail(String currentEmail, String newEmail){
        User user = userRepository.findByEmail(currentEmail).orElseThrow(() -> new RuntimeException("Uzytkownik nie istnieje"));

        if(!userRepository.existsByEmail(currentEmail)){
            throw new RuntimeException("Ten email jest już zajety");
        }

        user.setEmail(newEmail);
        userRepository.save(user);
    }

    public void updatePassword(String currentEmail, String oldPassword, String newPassword){
        User user = userRepository.findByEmail(currentEmail).orElseThrow(() -> new RuntimeException("Uzytkownik nie istnieje"));

        if(!passwordEncoder.matches(oldPassword, user.getPassword())){
            throw new RuntimeException("Podane haslo jest nieprawidlowe");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}