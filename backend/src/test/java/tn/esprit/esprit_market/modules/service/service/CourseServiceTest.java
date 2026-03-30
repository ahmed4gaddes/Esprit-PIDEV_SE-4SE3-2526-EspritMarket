package tn.esprit.esprit_market.modules.service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.esprit_market.modules.service.dto.CourseDTO;
import tn.esprit.esprit_market.modules.service.entity.Certificate;
import tn.esprit.esprit_market.modules.service.entity.Course;
import tn.esprit.esprit_market.modules.service.entity.Workshop;
import tn.esprit.esprit_market.modules.service.mapper.ServiceModuleMapper;
import tn.esprit.esprit_market.modules.service.repository.CertificateRepository;
import tn.esprit.esprit_market.modules.service.repository.CourseRepository;
import tn.esprit.esprit_market.modules.service.repository.WorkshopRepository;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;
    @Mock
    private WorkshopRepository workshopRepository;
    @Mock
    private CertificateRepository certificateRepository;
    @Mock
    private ServiceModuleMapper mapper;

    @InjectMocks
    private CourseService courseService;

    private Course course;
    private CourseDTO dto;

    @BeforeEach
    void setUp() {
        course = new Course();
        course.setId(2L);
        course.setTitle("Spring Concepts");

        dto = new CourseDTO();
        dto.setId(2L);
        dto.setTitle("Spring Concepts DTO");
        dto.setWorkshopId(10L);
        dto.setCertificateId(50L);
    }

    @Test
    void testGetAll() {
        when(courseRepository.findAll()).thenReturn(Arrays.asList(course));
        when(mapper.toDto(course)).thenReturn(dto);
        when(certificateRepository.findByRequiredCourses_Id(2L)).thenReturn(Arrays.asList());

        List<CourseDTO> results = courseService.getAll();

        assertEquals(1, results.size());
        assertEquals("Spring Concepts DTO", results.get(0).getTitle());
    }

    @Test
    void testGetById() {
        when(courseRepository.findById(2L)).thenReturn(Optional.of(course));
        when(mapper.toDto(course)).thenReturn(dto);
        when(certificateRepository.findByRequiredCourses_Id(2L)).thenReturn(Arrays.asList());

        CourseDTO result = courseService.getById(2L);

        assertNotNull(result);
        assertEquals(2L, result.getId());
    }

    @Test
    void testCreate() {
        Workshop w = new Workshop();
        w.setId(10L);
        
        Certificate c = new Certificate();
        c.setId(50L);
        c.setRequiredCourses(new HashSet<>());

        when(workshopRepository.findById(10L)).thenReturn(Optional.of(w));
        doNothing().when(mapper).toEntity(any(CourseDTO.class), any(Course.class));
        when(courseRepository.save(any(Course.class))).thenReturn(course);
        when(courseRepository.findById(2L)).thenReturn(Optional.of(course));
        when(certificateRepository.findByIdWithCourses(50L)).thenReturn(Optional.of(c));
        when(certificateRepository.save(any(Certificate.class))).thenReturn(c);
        when(mapper.toDto(course)).thenReturn(dto);

        CourseDTO result = courseService.create(dto);

        assertNotNull(result);
        verify(courseRepository).save(any(Course.class));
        verify(certificateRepository).save(any(Certificate.class));
    }

    @Test
    void testDelete() {
        when(courseRepository.findById(2L)).thenReturn(Optional.of(course));
        when(certificateRepository.findByRequiredCourses_Id(2L)).thenReturn(Arrays.asList());
        doNothing().when(courseRepository).delete(course);

        courseService.delete(2L);

        verify(courseRepository).delete(course);
    }
}
