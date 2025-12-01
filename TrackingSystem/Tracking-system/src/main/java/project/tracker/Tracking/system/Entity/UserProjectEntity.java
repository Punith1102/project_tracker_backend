package project.tracker.Tracking.system.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "user_project")
public class UserProjectEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_project_id")
    private Integer userProjectId;


    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;


    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private ProjectEntity project;


    @Column(nullable = false)
    private String role;
}