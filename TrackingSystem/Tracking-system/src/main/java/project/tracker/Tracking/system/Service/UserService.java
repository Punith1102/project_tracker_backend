package project.tracker.Tracking.system.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import project.tracker.Tracking.system.Entity.UserEntity;
import project.tracker.Tracking.system.Repository.UserRepository;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // 1. Get All Users (Used by Admin Dashboard to see list of employees)
    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }

    // 2. Get User by Email (Helper for Auth and Project services)
    public UserEntity getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    // 3. Get User by ID (Helper for lookups and connecting relations)
    public UserEntity getUserById(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
    }
}