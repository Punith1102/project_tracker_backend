package project.tracker.Tracking.system.tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import project.tracker.Tracking.system.Entity.ProjectEntity;
import project.tracker.Tracking.system.Entity.UserEntity;
import project.tracker.Tracking.system.Repository.ProjectRepository;
import project.tracker.Tracking.system.Repository.UserRepository;
import project.tracker.Tracking.system.Service.ProjectService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Project Service Tests")
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ProjectService projectService;

    private UserEntity testUser;
    private ProjectEntity testProject;

    @BeforeEach
    void setUp() {
        testUser = new UserEntity();
        testUser.setUserId(1);
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");

        testProject = new ProjectEntity();
        testProject.setProjectId(1);
        testProject.setName("Test Project");
        testProject.setDescription("Test Description");
        testProject.setCreatedBy(testUser);
    }

    @Test
    @DisplayName("Should create project successfully")
    void shouldCreateProject() {
        ProjectEntity newProject = new ProjectEntity();
        newProject.setName("New Project");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(projectRepository.save(any(ProjectEntity.class))).thenAnswer(i -> {
            ProjectEntity p = i.getArgument(0);
            p.setProjectId(1);
            return p;
        });

        ProjectEntity result = projectService.createProject(newProject, "test@example.com");

        assertNotNull(result);
        assertEquals(testUser, result.getCreatedBy());
    }

    @Test
    @DisplayName("Should get project by ID")
    void shouldGetProjectById() {
        when(projectRepository.findById(1)).thenReturn(Optional.of(testProject));

        ProjectEntity result = projectService.getProjectById(1);

        assertNotNull(result);
        assertEquals("Test Project", result.getName());
    }
}
