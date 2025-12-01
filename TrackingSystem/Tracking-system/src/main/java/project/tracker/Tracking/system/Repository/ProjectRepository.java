package project.tracker.Tracking.system.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import project.tracker.Tracking.system.Entity.ProjectEntity;
import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<ProjectEntity, Integer> {


    @Query("SELECT DISTINCT p FROM ProjectEntity p " +
            "LEFT JOIN UserProjectEntity up ON p.projectId = up.project.projectId " +
            "WHERE p.createdBy.userId = :userId " +
            "OR up.user.userId = :userId")
    List<ProjectEntity> findProjectsForUser(@Param("userId") Integer userId);


    List<ProjectEntity> findByCreatedByUserId(Integer userId);
}