package tn.esprit.esprit_market.modules.service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.esprit_market.exceptions.ResourceNotFoundException;
import tn.esprit.esprit_market.modules.service.dto.CourseDTO;
import tn.esprit.esprit_market.modules.service.entity.Course;
import tn.esprit.esprit_market.modules.service.entity.Workshop;
import tn.esprit.esprit_market.modules.service.mapper.ServiceModuleMapper;
import tn.esprit.esprit_market.modules.service.repository.CourseRepository;
import tn.esprit.esprit_market.modules.service.repository.WorkshopRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CourseService implements ICourseService {
    private final CourseRepository courseRepository;
    private final WorkshopRepository workshopRepository;
    private final ServiceModuleMapper mapper;

    @Transactional(readOnly = true)
    public List<CourseDTO> getAll() {
        return courseRepository.findAll()
                .stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CourseDTO getById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
        return mapper.toDto(course);
    }

    public CourseDTO create(CourseDTO dto) {
        Course course = new Course();
        mapper.toEntity(dto, course);

        if (dto.getWorkshopId() != null) {
            Workshop workshop = workshopRepository.findById(dto.getWorkshopId())
                    .orElseThrow(() -> new ResourceNotFoundException("Workshop not found: " + dto.getWorkshopId()));
            course.setWorkshop(workshop);
        }

        return mapper.toDto(courseRepository.save(course));
    }

    public CourseDTO update(Long id, CourseDTO dto) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));

        mapper.toEntity(dto, course);

        if (dto.getWorkshopId() != null
                && (course.getWorkshop() == null || !course.getWorkshop().getId().equals(dto.getWorkshopId()))) {
            Workshop workshop = workshopRepository.findById(dto.getWorkshopId())
                    .orElseThrow(() -> new ResourceNotFoundException("Workshop not found: " + dto.getWorkshopId()));
            course.setWorkshop(workshop);
        }

        return mapper.toDto(courseRepository.save(course));
    }

    public void delete(Long id) {
        if (!courseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cannot delete: Course not found with id: " + id);
        }
        courseRepository.deleteById(id);
    }
}