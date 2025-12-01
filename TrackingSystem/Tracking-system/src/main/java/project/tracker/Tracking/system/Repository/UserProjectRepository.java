package project.tracker.Tracking.system.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.tracker.Tracking.system.Entity.UserProjectEntity;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserProjectRepository extends JpaRepository<UserProjectEntity, Integer> {

    List<UserProjectEntity> findByUserUserId(Integer userId);
    List<UserProjectEntity> findByProjectProjectId(Integer projectId);
    Optional<UserProjectEntity> findByUserUserIdAndProjectProjectId(Integer userId, Integer projectId);
}