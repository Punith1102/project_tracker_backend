package project.tracker.Tracking.system.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import project.tracker.Tracking.system.Entity.ProjectEntity;
import project.tracker.Tracking.system.Entity.UserEntity;
import project.tracker.Tracking.system.Entity.UserProjectEntity;
import project.tracker.Tracking.system.Repository.ProjectRepository;
import project.tracker.Tracking.system.Repository.UserProjectRepository;
import project.tracker.Tracking.system.Repository.UserRepository;

import java.util.List;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserProjectRepository userProjectRepository;

    // 1. Get projects for a specific user (All projects for ADMIN, or Created/Assigned for USER)
    public List<ProjectEntity> getProjectsForUser(String email) {
        // First, look up the user by email to get their ID and role
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // If user is ADMIN, return ALL projects in the system
        if ("ADMIN".equals(user.getRole())) {
            System.out.println("DEBUG: User is ADMIN, returning all projects");
            return projectRepository.findAll();
        }

        // For regular users, return only projects they created or are assigned to
        System.out.println("DEBUG: User is regular USER, returning their projects");
        return projectRepository.findProjectsForUser(user.getUserId());
    }

    // 2. Get a single project by ID
    public ProjectEntity getProjectById(Integer projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found with ID: " + projectId));
    }

    // 3. Create a new project
    public ProjectEntity createProject(ProjectEntity project, String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Set the logged-in user as the creator
        project.setCreatedBy(user);

        return projectRepository.save(project);
    }

    // 4. Admin Feature: Get ALL projects in the system
    public List<ProjectEntity> getAllProjects() {
        return projectRepository.findAll();
    }

    // 5. Admin Feature: Add a member to a project
    public void addMember(Integer projectId, Integer userId) {
        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if the user is already a member to avoid duplicate entries
        if (userProjectRepository.findByUserUserIdAndProjectProjectId(userId, projectId).isPresent()) {
            throw new RuntimeException("User is already a member of this project");
        }

        // Create the link
        UserProjectEntity membership = new UserProjectEntity();
        membership.setUser(user);
        membership.setProject(project);
        membership.setRole("MEMBER"); // Default role

        userProjectRepository.save(membership);
    }

    // 6. Delete a project (Admins can delete any project)
    public void deleteProject(Integer projectId) {
        projectRepository.deleteById(projectId);
    }

    // 7. Update a project
    public ProjectEntity updateProject(Integer projectId, ProjectEntity projectUpdates) {
        ProjectEntity project = getProjectById(projectId);
        
        if (projectUpdates.getName() != null) {
            project.setName(projectUpdates.getName());
        }
        if (projectUpdates.getDescription() != null) {
            project.setDescription(projectUpdates.getDescription());
        }
        // Add other fields as necessary
        
        return projectRepository.save(project);
    }
}