package tn.esprit.esprit_market.modules.service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import tn.esprit.esprit_market.modules.service.dto.WorkshopDTO;
import tn.esprit.esprit_market.modules.service.service.IWorkshopService;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkshopControllerTest {

    @Mock
    private IWorkshopService workshopService;

    @InjectMocks
    private WorkshopController controller;

    private WorkshopDTO dto;

    @BeforeEach
    void setUp() {
        dto = new WorkshopDTO();
        dto.setId(20L);
        dto.setTitle("Spring Boot Workshop");
    }

    @Test
    void testGetAllWorkshops() {
        when(workshopService.getAll()).thenReturn(Arrays.asList(dto));

        ResponseEntity<List<WorkshopDTO>> response = controller.getAllWorkshops();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(workshopService).getAll();
    }

    @Test
    void testGetWorkshopById() {
        when(workshopService.getById(20L)).thenReturn(dto);

        ResponseEntity<WorkshopDTO> response = controller.getWorkshopById(20L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(20L, response.getBody().getId());
        verify(workshopService).getById(20L);
    }

    @Test
    void testCreateWorkshop() {
        when(workshopService.create(any(WorkshopDTO.class))).thenReturn(dto);

        ResponseEntity<WorkshopDTO> response = controller.createWorkshop(dto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Spring Boot Workshop", response.getBody().getTitle());
        verify(workshopService).create(any(WorkshopDTO.class));
    }

    @Test
    void testUpdateWorkshop() {
        when(workshopService.update(eq(20L), any(WorkshopDTO.class))).thenReturn(dto);

        ResponseEntity<WorkshopDTO> response = controller.updateWorkshop(20L, dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(20L, response.getBody().getId());
        verify(workshopService).update(eq(20L), any(WorkshopDTO.class));
    }

    @Test
    void testDeleteWorkshop() {
        doNothing().when(workshopService).delete(20L);

        ResponseEntity<Void> response = controller.deleteWorkshop(20L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(workshopService).delete(20L);
    }
}
