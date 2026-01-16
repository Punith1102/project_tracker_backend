package project.tracker.Tracking.system.tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import project.tracker.Tracking.system.Entity.UserEntity;
import project.tracker.Tracking.system.Repository.UserRepository;
import project.tracker.Tracking.system.Service.UserService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("User Service Tests")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private UserEntity testUser;

    @BeforeEach
    void setUp() {
        testUser = new UserEntity();
        testUser.setUserId(1);
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setRole("USER");
    }

    @Test
    @DisplayName("Should return all users")
    void shouldGetAllUsers() {
        UserEntity user2 = new UserEntity();
        user2.setUserId(2);
        user2.setName("User 2");

        when(userRepository.findAll()).thenReturn(Arrays.asList(testUser, user2));

        List<UserEntity> users = userService.getAllUsers();

        assertEquals(2, users.size());
        verify(userRepository).findAll();
    }
}
