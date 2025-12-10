package project.tracker.Tracking.system.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import project.tracker.Tracking.system.Entity.ProjectEntity;
import project.tracker.Tracking.system.Entity.TaskEntity;
import project.tracker.Tracking.system.Entity.TaskStatusEntity;
import project.tracker.Tracking.system.Entity.UserEntity;
import project.tracker.Tracking.system.Repository.ProjectRepository;
import project.tracker.Tracking.system.Repository.TaskRepository;
import project.tracker.Tracking.system.Repository.TaskStatusRepository;
import project.tracker.Tracking.system.Repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.function.Supplier;

@Service
public class TaskService {

    @Autowired private TaskRepository taskRepository;
    @Autowired private ProjectRepository projectRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private TaskStatusRepository taskStatusRepository;

    private final Supplier<RuntimeException> taskNotFound = () -> new RuntimeException("Task not found");
    private final Supplier<RuntimeException> userNotFound = () -> new RuntimeException("User not found");
    private final Supplier<RuntimeException> projectNotFound = () -> new RuntimeException("Project not found");


    public TaskEntity createTask(Integer projectId, TaskEntity task, String email) {
        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(projectNotFound);

        UserEntity creator = userRepository.findByEmail(email)
                .orElseThrow(userNotFound);

        if (task.getStatus() == null) {
            TaskStatusEntity defaultStatus = taskStatusRepository.findById(1)
                    .orElseThrow(() -> new RuntimeException("Default status (ID 1) not found. Seed task_status table."));
            task.setStatus(defaultStatus);
        }

        task.setProject(project);
        task.setCreatedBy(creator);

        if (task.getAssignedTo() != null && task.getAssignedTo().getUserId() != null) {
            UserEntity assignee = userRepository.findById(task.getAssignedTo().getUserId())
                    .orElseThrow(() -> new RuntimeException("Assignee user not found."));
            task.setAssignedTo(assignee);
        } else {
            task.setAssignedTo(null);
        }

        return taskRepository.save(task);
    }

    public List<TaskEntity> getTasksAssignedToUser(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(userNotFound);

        return taskRepository.findByAssignedToUserId(user.getUserId());
    }

    public TaskEntity updateTaskStatus(Integer taskId, Integer statusId) {
        TaskEntity task = taskRepository.findById(taskId)
                .orElseThrow(taskNotFound);

        TaskStatusEntity newStatus = taskStatusRepository.findById(statusId)
                .orElseThrow(() -> new RuntimeException("Status not found"));

        task.setStatus(newStatus);
        return taskRepository.save(task);
    }

    public TaskEntity updateTaskDetails(Integer taskId, TaskEntity taskUpdates) {
        TaskEntity task = taskRepository.findById(taskId)
                .orElseThrow(taskNotFound);

        if (taskUpdates.getTitle() != null) task.setTitle(taskUpdates.getTitle());
        if (taskUpdates.getDescription() != null) task.setDescription(taskUpdates.getDescription());
        if (taskUpdates.getPriority() != null) task.setPriority(taskUpdates.getPriority());
        if (taskUpdates.getDueDate() != null) task.setDueDate(taskUpdates.getDueDate());

        if (taskUpdates.getAssignedTo() != null) {
            if (taskUpdates.getAssignedTo().getUserId() != null) {
                UserEntity assignee = userRepository.findById(taskUpdates.getAssignedTo().getUserId())
                        .orElseThrow(userNotFound);
                task.setAssignedTo(assignee);
            } else {
                task.setAssignedTo(null);
            }
        }

        return taskRepository.save(task);
    }

    public TaskEntity unassignUserFromTask(Integer taskId) {
        TaskEntity task = taskRepository.findById(taskId)
                .orElseThrow(taskNotFound);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName();
        UserEntity currentUser = userRepository.findByEmail(currentUserName)
                .orElseThrow(userNotFound);

        if (!"ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            throw new RuntimeException("Access Denied: Only Admin can unassign users from tasks.");
        }

        task.setAssignedTo(null);
        return taskRepository.save(task);
    }

    @org.springframework.transaction.annotation.Transactional
    public void deleteTask(Integer taskId) {
        TaskEntity task = taskRepository.findById(taskId)
                .orElseThrow(taskNotFound);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName();
        UserEntity currentUser = userRepository.findByEmail(currentUserName)
                .orElseThrow(userNotFound);

        boolean isAdmin = "ADMIN".equalsIgnoreCase(currentUser.getRole());
        boolean isCreator = task.getCreatedBy().getUserId().equals(currentUser.getUserId());
        boolean isProjectCreator = task.getProject().getCreatedBy().getUserId().equals(currentUser.getUserId());
        boolean isAssignee = task.getAssignedTo() != null && task.getAssignedTo().getUserId().equals(currentUser.getUserId());

        if (isAdmin || isCreator || isProjectCreator || isAssignee) {
            task.getProject().getTasks().remove(task);
            taskRepository.delete(task);
        } else {
            throw new RuntimeException("Access Denied: Only Admin, Project Creator, Task Creator, or Assignee can delete this task.");
        }
    }
}