package project.tracker.Tracking.system.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.tracker.Tracking.system.Entity.ProjectEntity;
import project.tracker.Tracking.system.Entity.TaskEntity;
import project.tracker.Tracking.system.Service.ProjectService;
import project.tracker.Tracking.system.Service.TaskService;
import project.tracker.Tracking.system.Service.UserService;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserService userService;


    @GetMapping
    public ResponseEntity<List<ProjectEntity>> getMyProjects(Principal principal) {
        String email = principal.getName();
        return ResponseEntity.ok(projectService.getProjectsForUser(email));
    }


    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectEntity> getProjectById(@PathVariable Integer projectId) {
        return ResponseEntity.ok(projectService.getProjectById(projectId));
    }


    @PostMapping
    public ResponseEntity<ProjectEntity> createProject(@RequestBody ProjectEntity project, Principal principal) {
        String email = principal.getName();
        return ResponseEntity.ok(projectService.createProject(project, email));
    }


    @PostMapping("/{projectId}/tasks")
    public ResponseEntity<TaskEntity> createTask(
            @PathVariable Integer projectId,
            @RequestBody TaskEntity task,
            Principal principal) {

        System.out.println("DEBUG: createTask called for projectId: " + projectId);
        System.out.println("DEBUG: Principal: " + principal);
        if (principal == null) {
            System.out.println("ERROR: Principal is NULL! User not authenticated.");
            return ResponseEntity.status(403).build();
        }
        
        String email = principal.getName();
        System.out.println("DEBUG: User email from Principal: " + email);
        return ResponseEntity.ok(taskService.createTask(projectId, task, email));
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<Void> deleteProject(@PathVariable Integer projectId) {
        projectService.deleteProject(projectId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{projectId}")
    public ResponseEntity<ProjectEntity> updateProject(
            @PathVariable Integer projectId,
            @RequestBody ProjectEntity projectUpdates) {
        return ResponseEntity.ok(projectService.updateProject(projectId, projectUpdates));
    }
}