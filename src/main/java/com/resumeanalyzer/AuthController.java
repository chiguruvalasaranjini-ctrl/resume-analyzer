package com.resumeanalyzer.resumeanalyzer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody User user) {
        Map<String, String> response = new HashMap<>();

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            response.put("message", "Email already exists!");
            return ResponseEntity.badRequest().body(response);
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        response.put("message", "User registered successfully!");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody User user) {
        Map<String, String> response = new HashMap<>();

        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());

        if (existingUser.isEmpty()) {
            response.put("message", "User not found!");
            return ResponseEntity.badRequest().body(response);
        }

        if (!passwordEncoder.matches(user.getPassword(), existingUser.get().getPassword())) {
            response.put("message", "Wrong password!");
            return ResponseEntity.badRequest().body(response);
        }

        response.put("message", "Login successful!");
        response.put("name", existingUser.get().getName());
        response.put("email", existingUser.get().getEmail());
        response.put("plan", existingUser.get().getPlan());
        return ResponseEntity.ok(response);
    }
}