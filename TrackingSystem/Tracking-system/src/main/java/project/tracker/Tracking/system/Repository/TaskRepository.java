package project.tracker.Tracking.system.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.tracker.Tracking.system.Entity.TaskEntity;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, Integer> {

    List<TaskEntity> findByProjectProjectId(Integer projectId);

    List<TaskEntity> findByAssignedToUserId(Integer userId);

    List<TaskEntity> findByProjectProjectIdAndStatusName(Integer projectId, String statusName);
}