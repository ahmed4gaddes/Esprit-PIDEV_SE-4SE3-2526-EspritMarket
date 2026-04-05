package tn.esprit.esprit_market.modules.service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import tn.esprit.esprit_market.modules.service.dto.CourseDTO;
import tn.esprit.esprit_market.modules.service.service.ICourseService;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseControllerTest {

    @Mock
    private ICourseService courseService;

    @InjectMocks
    private CourseController courseController;

    private CourseDTO fakeCourseDTO;

    @BeforeEach
    void setUp() {
        fakeCourseDTO = new CourseDTO();
        fakeCourseDTO.setId(10L);
        fakeCourseDTO.setTitle("Spring Boot Advanced");
        fakeCourseDTO.setDescription("Learn Spring Boot in depth");
    }

    @Test
    void testGetAllCourses() {
        when(courseService.getAll()).thenReturn(Arrays.asList(fakeCourseDTO));

        ResponseEntity<List<CourseDTO>> response = courseController.getAllCourses();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(courseService, times(1)).getAll();
    }

    @Test
    void testGetCourseById() {
        when(courseService.getById(10L)).thenReturn(fakeCourseDTO);

        ResponseEntity<CourseDTO> response = courseController.getCourseById(10L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(10L, response.getBody().getId());
        verify(courseService, times(1)).getById(10L);
    }

    @Test
    void testCreateCourse() {
        when(courseService.create(any(CourseDTO.class))).thenReturn(fakeCourseDTO);

        ResponseEntity<CourseDTO> response = courseController.createCourse(fakeCourseDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Spring Boot Advanced", response.getBody().getTitle());
        verify(courseService, times(1)).create(any(CourseDTO.class));
    }

    @Test
    void testUpdateCourse() {
        when(courseService.update(eq(10L), any(CourseDTO.class))).thenReturn(fakeCourseDTO);

        ResponseEntity<CourseDTO> response = courseController.updateCourse(10L, fakeCourseDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(10L, response.getBody().getId());
        verify(courseService, times(1)).update(eq(10L), any(CourseDTO.class));
    }

    @Test
    void testDeleteCourse() {
        doNothing().when(courseService).delete(10L);

        ResponseEntity<Void> response = courseController.deleteCourse(10L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(courseService, times(1)).delete(10L);
    }
}
