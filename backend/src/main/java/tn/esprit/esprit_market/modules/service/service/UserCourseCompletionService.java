package tn.esprit.esprit_market.modules.service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.esprit_market.exceptions.ResourceNotFoundException;
import tn.esprit.esprit_market.modules.service.dto.UserCourseCompletionDTO;
import tn.esprit.esprit_market.modules.service.entity.Course;
import tn.esprit.esprit_market.modules.service.entity.UserCourseCompletion;
import tn.esprit.esprit_market.modules.service.repository.CourseRepository;
import tn.esprit.esprit_market.modules.service.repository.UserCourseCompletionRepository;
import tn.esprit.esprit_market.modules.user.entity.User;
import tn.esprit.esprit_market.modules.user.repository.UserRepository;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserCourseCompletionService {

    private final UserCourseCompletionRepository userCourseCompletionRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    public UserCourseCompletionDTO markComplete(Long userId, Long courseId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        return userCourseCompletionRepository.findByUser_IdAndCourse_Id(userId, courseId)
                .map(this::toDto)
                .orElseGet(() -> toDto(userCourseCompletionRepository.save(UserCourseCompletion.builder()
                        .user(user)
                        .course(course)
                        .completedAt(new Date())
                        .build())));
    }

    @Transactional(readOnly = true)
    public List<UserCourseCompletionDTO> listForUser(Long userId) {
        return userCourseCompletionRepository.findByUser_IdOrderByCompletedAtDesc(userId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private UserCourseCompletionDTO toDto(UserCourseCompletion entity) {
        return UserCourseCompletionDTO.builder()
                .id(entity.getId())
                .userId(entity.getUser().getId())
                .courseId(entity.getCourse().getId())
                .courseTitle(entity.getCourse().getTitle())
                .completedAt(entity.getCompletedAt())
                .build();
    }
}
