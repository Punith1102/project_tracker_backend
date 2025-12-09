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

    public List<ProjectEntity> getProjectsForUser(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if ("ADMIN".equals(user.getRole())) {
            System.out.println("DEBUG: User is ADMIN, returning all projects");
            return projectRepository.findAll();
        }

        System.out.println("DEBUG: User is regular USER, returning their projects");
        return projectRepository.findProjectsForUser(user.getUserId());
    }

    public ProjectEntity getProjectById(Integer projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found with ID: " + projectId));
    }

    public ProjectEntity createProject(ProjectEntity project, String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        project.setCreatedBy(user);

        return projectRepository.save(project);
    }

    public List<ProjectEntity> getAllProjects() {
        return projectRepository.findAll();
    }

    public void addMember(Integer projectId, Integer userId) {
        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (userProjectRepository.findByUserUserIdAndProjectProjectId(userId, projectId).isPresent()) {
            throw new RuntimeException("User is already a member of this project");
        }

        UserProjectEntity membership = new UserProjectEntity();
        membership.setUser(user);
        membership.setProject(project);
        membership.setRole("MEMBER");

        userProjectRepository.save(membership);
    }

    public void deleteProject(Integer projectId) {
        projectRepository.deleteById(projectId);
    }

    public ProjectEntity updateProject(Integer projectId, ProjectEntity projectUpdates) {
        ProjectEntity project = getProjectById(projectId);
        
        if (projectUpdates.getName() != null) {
            project.setName(projectUpdates.getName());
        }
        if (projectUpdates.getDescription() != null) {
            project.setDescription(projectUpdates.getDescription());
        }
        
        return projectRepository.save(project);
    }
}