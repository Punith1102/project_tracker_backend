package project.tracker.Tracking.system.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import project.tracker.Tracking.system.dto.AuthResponse;
import project.tracker.Tracking.system.dto.LoginRequest;
import project.tracker.Tracking.system.dto.SignupRequest;
import project.tracker.Tracking.system.Entity.UserEntity;
import project.tracker.Tracking.system.Repository.UserRepository;
import project.tracker.Tracking.system.util.JwtUtils;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;


    public String registerUser(SignupRequest request) {
        // Validation: Check if email exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Error: Email is already in use!");
        }


        UserEntity user = new UserEntity();
        user.setName(request.getName());
        user.setEmail(request.getEmail());


        user.setPassword(passwordEncoder.encode(request.getPassword()));


        user.setRole("USER");

        userRepository.save(user);
        return "User registered successfully!";
    }


    public AuthResponse loginUser(LoginRequest request) {


        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );


        SecurityContextHolder.getContext().setAuthentication(authentication);


        String jwt = jwtUtils.generateJwtToken(authentication);

        // Fetch user details to include in response
        UserEntity userDetails = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Error: User not found."));

        return new AuthResponse(
                jwt,
                userDetails.getEmail(),
                userDetails.getName(),
                userDetails.getRole(),
                userDetails.getUserId()
        );
    }
}