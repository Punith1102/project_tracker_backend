package project.tracker.Tracking.system.tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import project.tracker.Tracking.system.Entity.ProjectEntity;
import project.tracker.Tracking.system.Entity.TaskEntity;
import project.tracker.Tracking.system.Entity.TaskStatusEntity;
import project.tracker.Tracking.system.Entity.UserEntity;
import project.tracker.Tracking.system.Repository.ProjectRepository;
import project.tracker.Tracking.system.Repository.TaskRepository;
import project.tracker.Tracking.system.Repository.TaskStatusRepository;
import project.tracker.Tracking.system.Repository.UserRepository;
import project.tracker.Tracking.system.Service.TaskService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Task Service Tests")
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TaskStatusRepository taskStatusRepository;

    @InjectMocks
    private TaskService taskService;

    private UserEntity testUser;
    private ProjectEntity testProject;
    private TaskEntity testTask;
    private TaskStatusEntity defaultStatus;

    @BeforeEach
    void setUp() {
        testUser = new UserEntity();
        testUser.setUserId(1);
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");

        testProject = new ProjectEntity();
        testProject.setProjectId(1);
        testProject.setName("Test Project");
        testProject.setCreatedBy(testUser);

        defaultStatus = new TaskStatusEntity();
        defaultStatus.setStatusId(1);
        defaultStatus.setName("Pending");

        testTask = new TaskEntity();
        testTask.setTaskId(1);
        testTask.setTitle("Test Task");
        testTask.setProject(testProject);
        testTask.setCreatedBy(testUser);
        testTask.setStatus(defaultStatus);
    }

    @Test
    @DisplayName("Should create task for project")
    void shouldCreateTask() {
        TaskEntity newTask = new TaskEntity();
        newTask.setTitle("New Task");

        when(projectRepository.findById(1)).thenReturn(Optional.of(testProject));
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(taskStatusRepository.findById(1)).thenReturn(Optional.of(defaultStatus));
        when(taskRepository.save(any(TaskEntity.class))).thenAnswer(i -> {
            TaskEntity t = i.getArgument(0);
            t.setTaskId(1);
            return t;
        });

        TaskEntity result = taskService.createTask(1, newTask, "test@example.com");

        assertNotNull(result);
        assertEquals(testProject, result.getProject());
    }

    @Test
    @DisplayName("Should update task status")
    void shouldUpdateTaskStatus() {
        TaskStatusEntity newStatus = new TaskStatusEntity();
        newStatus.setStatusId(2);
        newStatus.setName("In Progress");

        when(taskRepository.findById(1)).thenReturn(Optional.of(testTask));
        when(taskStatusRepository.findById(2)).thenReturn(Optional.of(newStatus));
        when(taskRepository.save(any(TaskEntity.class))).thenAnswer(i -> i.getArgument(0));

        TaskEntity result = taskService.updateTaskStatus(1, 2);

        assertEquals("In Progress", result.getStatus().getName());
    }

    @Test
    @DisplayName("Should get tasks assigned to user")
    void shouldGetAssignedTasks() {
        testTask.setAssignedTo(testUser);

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(taskRepository.findByAssignedToUserId(1)).thenReturn(Arrays.asList(testTask));

        List<TaskEntity> tasks = taskService.getTasksAssignedToUser("test@example.com");

        assertEquals(1, tasks.size());
        assertEquals("Test Task", tasks.get(0).getTitle());
    }
}
