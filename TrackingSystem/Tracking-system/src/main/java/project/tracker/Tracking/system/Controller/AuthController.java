package project.tracker.Tracking.system.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import project.tracker.Tracking.system.dto.*;
import project.tracker.Tracking.system.Service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    // 1. REGISTER ENDPOINT
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody SignupRequest signUpRequest) {
        try {
            // Call the service to handle registration logic
            String responseMessage = authService.registerUser(signUpRequest);
            return ResponseEntity.ok(responseMessage);
        } catch (RuntimeException e) {
            // Catch errors (like "Email already in use") and send 400 Bad Request
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 2. LOGIN ENDPOINT
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        try {
            // Call the service to get the JWT token
            AuthResponse response = authService.loginUser(loginRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Return 401 Unauthorized for invalid credentials
            System.out.println("Login failed: " + e.getMessage());
            return ResponseEntity.status(401).body("Invalid email or password");
        }
    }

    @Autowired
    private project.tracker.Tracking.system.Service.UserService userService;

    // 3. GET ALL USERS (Public/Authenticated)
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
}