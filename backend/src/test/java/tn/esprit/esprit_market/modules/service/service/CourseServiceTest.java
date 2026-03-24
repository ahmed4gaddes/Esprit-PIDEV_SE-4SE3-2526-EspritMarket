package tn.esprit.esprit_market.modules.service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.esprit_market.exceptions.ResourceNotFoundException;
import tn.esprit.esprit_market.modules.service.dto.CourseDTO;
import tn.esprit.esprit_market.modules.service.entity.Course;
import tn.esprit.esprit_market.modules.service.entity.Workshop;
import tn.esprit.esprit_market.modules.service.mapper.ServiceModuleMapper;
import tn.esprit.esprit_market.modules.service.repository.CourseRepository;
import tn.esprit.esprit_market.modules.service.repository.WorkshopRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private WorkshopRepository workshopRepository;

    @Mock
    private ServiceModuleMapper mapper;

    @InjectMocks
    private CourseService courseService;

    private Course course;
    private CourseDTO courseDTO;
    private Workshop workshop;

    @BeforeEach
    void setUp() {
        workshop = new Workshop();
        workshop.setId(1L);

        course = new Course();
        course.setId(1L);
        course.setTitle("Test Course");
        course.setWorkshop(workshop);

        courseDTO = new CourseDTO();
        courseDTO.setId(1L);
        courseDTO.setTitle("Test Course");
        courseDTO.setWorkshopId(1L);
    }

    @Test
    void getAll_ShouldReturnListOfCourseDTOs() {
        when(courseRepository.findAll()).thenReturn(Arrays.asList(course));
        when(mapper.toDto(any(Course.class))).thenReturn(courseDTO);

        List<CourseDTO> result = courseService.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(courseRepository, times(1)).findAll();
    }

    @Test
    void getById_WhenExists_ShouldReturnCourseDTO() {
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(mapper.toDto(any(Course.class))).thenReturn(courseDTO);

        CourseDTO result = courseService.getById(1L);

        assertNotNull(result);
        assertEquals("Test Course", result.getTitle());
    }

    @Test
    void create_WhenWorkshopExists_ShouldSaveAndReturnCourseDTO() {
        when(workshopRepository.findById(1L)).thenReturn(Optional.of(workshop));
        when(courseRepository.save(any(Course.class))).thenReturn(course);
        when(mapper.toDto(any(Course.class))).thenReturn(courseDTO);

        CourseDTO result = courseService.create(courseDTO);

        assertNotNull(result);
        verify(workshopRepository, times(1)).findById(1L);
        verify(courseRepository, times(1)).save(any(Course.class));
    }

    @Test
    void update_WhenExists_ShouldUpdateAndReturnCourseDTO() {
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(courseRepository.save(any(Course.class))).thenReturn(course);
        when(mapper.toDto(any(Course.class))).thenReturn(courseDTO);

        CourseDTO result = courseService.update(1L, courseDTO);

        assertNotNull(result);
        verify(courseRepository, times(1)).save(any(Course.class));
    }

    @Test
    void delete_WhenExists_ShouldDeleteCourse() {
        when(courseRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> courseService.delete(1L));
        verify(courseRepository, times(1)).deleteById(1L);
    }
}
