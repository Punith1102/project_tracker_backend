package project.tracker.Tracking.system.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.tracker.Tracking.system.Entity.TaskEntity;
import project.tracker.Tracking.system.Service.TaskService;

import java.util.List;
import java.util.Map;
import java.security.Principal;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @GetMapping("/assigned")
    public ResponseEntity<List<TaskEntity>> getMyAssignedTasks(Principal principal) {
        String email = principal.getName();
        return ResponseEntity.ok(taskService.getTasksAssignedToUser(email));
    }

    @PutMapping("/{taskId}/status")
    public ResponseEntity<TaskEntity> updateTaskStatus(
            @PathVariable Integer taskId,
            @RequestBody Map<String, Integer> requestBody) {

        Integer newStatusId = requestBody.get("statusId");
        return ResponseEntity.ok(taskService.updateTaskStatus(taskId, newStatusId));
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<TaskEntity> updateTaskDetails(
            @PathVariable Integer taskId,
            @RequestBody TaskEntity taskUpdates) {

        return ResponseEntity.ok(taskService.updateTaskDetails(taskId, taskUpdates));
    }

    @PutMapping("/{taskId}/unassign")
    public ResponseEntity<TaskEntity> unassignUser(@PathVariable Integer taskId) {
        return ResponseEntity.ok(taskService.unassignUserFromTask(taskId));
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable Integer taskId) {
        taskService.deleteTask(taskId);
        return ResponseEntity.noContent().build();
    }
}