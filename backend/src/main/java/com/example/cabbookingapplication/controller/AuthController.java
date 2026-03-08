package com.example.cabbookingapplication.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.cabbookingapplication.entity.Driver;
import com.example.cabbookingapplication.entity.User;
import com.example.cabbookingapplication.enums.Role;
import com.example.cabbookingapplication.repository.DriverRepository;
import com.example.cabbookingapplication.repository.UserRepository;
import com.example.cabbookingapplication.security.JwtUtil;
import com.example.cabbookingapplication.service.EmailService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private DriverRepository driverRepo;

    @PostMapping("/register")
    public String register(@RequestBody User user) {
        user.setPassword(encoder.encode(user.getPassword()));
        User savedUser = userRepo.save(user);

        if (savedUser.getRole() == Role.DRIVER) {
            Driver driver = new Driver();
            driver.setUser(savedUser);
            driverRepo.save(driver);
        }

        return "User registered";
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody User req) {

        User user = userRepo.findByEmail(req.getEmail())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.UNAUTHORIZED,
                                "Invalid email or password"
                        )
                );

        if (!user.isEnabled()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Your account has been disabled by admin"
            );
        }

        if (!encoder.matches(req.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid email or password"
            );
        }

        String token = jwtUtil.generateToken(
                user.getEmail(),
                user.getRole().name()
        );

        return ResponseEntity.ok(Map.of("token", token));
    }

    @PostMapping("/set-role")
    public Map<String, String> setRole(@RequestBody Map<String, String> req) {
        String role = req.get("role");

        User user = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        Role selectedRole = Role.valueOf(role);
        user.setRole(selectedRole);
        userRepo.save(user);

        if (selectedRole == Role.DRIVER) {
            driverRepo.findByUser(user)
                .orElseGet(() -> {
                    Driver driver = new Driver();
                    driver.setUser(user);
                    return driverRepo.save(driver);
                });
        }

        String token = jwtUtil.generateToken(
                user.getEmail(),
                user.getRole().name()
        );

        return Map.of("token", token);
    }

    @GetMapping("/me")
    public Map<String, Object> me() {

        Object principal = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        if (!(principal instanceof User user)) {
            throw new RuntimeException("Unauthorized");
        }

        return Map.of(
            "id", user.getId(),
            "name", user.getName(),
            "email", user.getEmail(),
            "role", user.getRole()
        );
    }

    @PostMapping("/forgot-password")
    public Map<String, String> forgotPassword(@RequestBody Map<String, String> req) {

        String email = req.get("email");

        userRepo.findByEmail(email).ifPresent(user -> {
            String token = jwtUtil.generateResetToken(email);
            emailService.sendResetEmail(email, token);
        });

        return Map.of("message", "If email exists, reset link sent");
    }

    @PostMapping("/reset-password")
    public Map<String, String> resetPassword(@RequestBody Map<String, String> req) {

        String token = req.get("token");
        String newPassword = req.get("newPassword");

        if (!jwtUtil.isResetToken(token)) {
            throw new RuntimeException("Invalid token");
        }

        String email = jwtUtil.extractEmail(token);

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(encoder.encode(newPassword));
        userRepo.save(user);

        return Map.of("message", "Password reset successful");
    }
}
