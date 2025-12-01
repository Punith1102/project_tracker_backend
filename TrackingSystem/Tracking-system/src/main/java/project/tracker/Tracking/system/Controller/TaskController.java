package project.tracker.Tracking.system.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.tracker.Tracking.system.Entity.TaskEntity;
import project.tracker.Tracking.system.Service.TaskService;

import java.util.List; // <--- Added this missing import
import java.util.Map;
import java.security.Principal;

@RestController
@RequestMapping("/api/tasks")
//@CrossOrigin("*") // DELETE THIS LINE (Handled by CorsConfig)
public class TaskController {

    @Autowired
    private TaskService taskService;

    // --- Existing Task Methods ---

    // 1. Get My Assigned Tasks (For dashboard widget)
    @GetMapping("/assigned")
    public ResponseEntity<List<TaskEntity>> getMyAssignedTasks(Principal principal) {
        String email = principal.getName();
        return ResponseEntity.ok(taskService.getTasksAssignedToUser(email));
    }

    // 2. Update Task Status (User operation: To Do -> Done)
    @PutMapping("/{taskId}/status")
    public ResponseEntity<TaskEntity> updateTaskStatus(
            @PathVariable Integer taskId,
            @RequestBody Map<String, Integer> requestBody) {

        Integer newStatusId = requestBody.get("statusId");
        return ResponseEntity.ok(taskService.updateTaskStatus(taskId, newStatusId));
    }

    // 3. Update Task Details (Title, Description, Assignee)
    @PutMapping("/{taskId}")
    public ResponseEntity<TaskEntity> updateTaskDetails(
            @PathVariable Integer taskId,
            @RequestBody TaskEntity taskUpdates) {

        return ResponseEntity.ok(taskService.updateTaskDetails(taskId, taskUpdates));
    }

    // --- Admin/Advanced Operations ---

    // 4. Admin Feature: Remove Assignee (Set assignedTo = null)
    @PutMapping("/{taskId}/unassign")
    public ResponseEntity<TaskEntity> unassignUser(@PathVariable Integer taskId) {
        // Permission check (Admin check) is done inside the TaskService method
        return ResponseEntity.ok(taskService.unassignUserFromTask(taskId));
    }

    // 5. Delete a Task (Admin or Creator only)
    @DeleteMapping("/{taskId}")
    public ResponseEntity<?> deleteTask(@PathVariable Integer taskId) {
        // Logic handles Admin/Creator check in the service layer
        taskService.deleteTask(taskId);
        return ResponseEntity.ok("Task deleted successfully");
    }
}