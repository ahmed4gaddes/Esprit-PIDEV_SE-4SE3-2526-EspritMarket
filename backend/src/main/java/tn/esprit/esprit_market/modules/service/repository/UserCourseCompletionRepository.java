package tn.esprit.esprit_market.modules.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.esprit_market.modules.service.entity.UserCourseCompletion;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserCourseCompletionRepository extends JpaRepository<UserCourseCompletion, Long> {

    boolean existsByUser_IdAndCourse_Id(Long userId, Long courseId);

    Optional<UserCourseCompletion> findByUser_IdAndCourse_Id(Long userId, Long courseId);

    List<UserCourseCompletion> findByUser_IdOrderByCompletedAtDesc(Long userId);

    List<UserCourseCompletion> findByUser_Id(Long userId);
}
