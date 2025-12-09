package project.tracker.Tracking.system.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import project.tracker.Tracking.system.Entity.ProjectEntity;
import project.tracker.Tracking.system.Entity.UserEntity;
import project.tracker.Tracking.system.Service.ProjectService;
import project.tracker.Tracking.system.Service.UserService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserService userService;


    @GetMapping("/users")
    public ResponseEntity<List<UserEntity>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }


    @GetMapping("/projects")
    public ResponseEntity<List<ProjectEntity>> getAllProjects() {
        return ResponseEntity.ok(projectService.getAllProjects());
    }


    @PostMapping("/projects/{projectId}/members")
    public ResponseEntity<?> addMemberToProject(
            @PathVariable Integer projectId,
            @RequestBody Map<String, Integer> requestBody) {

        Integer userId = requestBody.get("userId");
        projectService.addMember(projectId, userId);

        return ResponseEntity.ok("User assigned to project successfully");
    }
}