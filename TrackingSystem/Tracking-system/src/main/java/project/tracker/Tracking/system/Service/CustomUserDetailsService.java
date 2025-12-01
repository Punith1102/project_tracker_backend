package project.tracker.Tracking.system.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import project.tracker.Tracking.system.Entity.UserEntity;
import project.tracker.Tracking.system.Repository.UserRepository;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        // 1. Fetch the user from the database by Email
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // 2. Convert it into a Spring Security "User" object
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(), // The hashed password from DB
                Collections.singletonList(new SimpleGrantedAuthority(user.getRole())) // e.g., "ADMIN" or "USER"
        );
    }
}