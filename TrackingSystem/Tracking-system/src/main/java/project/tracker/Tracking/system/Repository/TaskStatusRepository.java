package project.tracker.Tracking.system.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.tracker.Tracking.system.Entity.TaskStatusEntity;
import java.util.Optional;

@Repository
public interface TaskStatusRepository extends JpaRepository<TaskStatusEntity, Integer> {

    Optional<TaskStatusEntity> findByName(String name);
}