package tn.esprit.esprit_market.modules.service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.esprit_market.exceptions.ResourceNotFoundException;
import tn.esprit.esprit_market.modules.service.dto.CourseDTO;
import tn.esprit.esprit_market.modules.service.entity.Certificate;
import tn.esprit.esprit_market.modules.service.entity.Course;
import tn.esprit.esprit_market.modules.service.entity.Workshop;
import tn.esprit.esprit_market.modules.service.mapper.ServiceModuleMapper;
import tn.esprit.esprit_market.modules.service.repository.CertificateRepository;
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
    private final CertificateRepository certificateRepository;
    private final ServiceModuleMapper mapper;

    @Transactional(readOnly = true)
    public List<CourseDTO> getAll() {
        return courseRepository.findAll()
                .stream()
                .map(c -> enrichCertificateId(mapper.toDto(c), c.getId()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CourseDTO getById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
        return enrichCertificateId(mapper.toDto(course), course.getId());
    }

    public CourseDTO create(CourseDTO dto) {
        Course course = new Course();
        mapper.toEntity(dto, course);

        if (dto.getWorkshopId() != null) {
            Workshop workshop = workshopRepository.findById(dto.getWorkshopId())
                    .orElseThrow(() -> new ResourceNotFoundException("Workshop not found: " + dto.getWorkshopId()));
            course.setWorkshop(workshop);
        }

        Course saved = courseRepository.save(course);
        syncCertificateAssociation(saved.getId(), dto.getCertificateId());
        return enrichCertificateId(mapper.toDto(saved), saved.getId());
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

        Course saved = courseRepository.save(course);
        syncCertificateAssociation(id, dto.getCertificateId());
        return enrichCertificateId(mapper.toDto(saved), saved.getId());
    }

    public void delete(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot delete: Course not found with id: " + id));
        removeCourseFromAllCertificates(course);
        courseRepository.delete(course);
    }

    private CourseDTO enrichCertificateId(CourseDTO dto, Long courseId) {
        certificateRepository.findByRequiredCourses_Id(courseId).stream()
                .findFirst()
                .ifPresent(cert -> dto.setCertificateId(cert.getId()));
        return dto;
    }

    /**
     * Met à jour la collection {@link Certificate#getRequiredCourses()} pour refléter le certificat choisi.
     */
    private void syncCertificateAssociation(Long courseId, Long newCertificateId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));
        removeCourseFromAllCertificates(course);
        if (newCertificateId != null) {
            Certificate cert = certificateRepository.findByIdWithCourses(newCertificateId)
                    .orElseThrow(() -> new ResourceNotFoundException("Certificate not found: " + newCertificateId));
            cert.getRequiredCourses().add(course);
            certificateRepository.save(cert);
        }
    }

    private void removeCourseFromAllCertificates(Course course) {
        for (Certificate c : certificateRepository.findByRequiredCourses_Id(course.getId())) {
            c.getRequiredCourses().removeIf(co -> co.getId().equals(course.getId()));
            certificateRepository.save(c);
        }
    }
}