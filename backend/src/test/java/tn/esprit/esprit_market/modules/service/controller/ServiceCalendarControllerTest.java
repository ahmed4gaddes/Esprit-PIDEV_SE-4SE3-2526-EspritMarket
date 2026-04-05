package tn.esprit.esprit_market.modules.service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import tn.esprit.esprit_market.modules.service.dto.ServiceCalendarDTO;
import tn.esprit.esprit_market.modules.service.service.IServiceCalendarService;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceCalendarControllerTest {

    @Mock private IServiceCalendarService calendarService;
    @InjectMocks private ServiceCalendarController controller;
    private ServiceCalendarDTO dto;

    @BeforeEach
    void setUp() { dto = new ServiceCalendarDTO(); dto.setId(1L); }

    @Test void testGetAll() {
        when(calendarService.getAll()).thenReturn(Arrays.asList(dto));
        ResponseEntity<List<ServiceCalendarDTO>> res = controller.getAllCalendars();
        assertEquals(HttpStatus.OK, res.getStatusCode());
    }

    @Test void testGetById() {
        when(calendarService.getById(1L)).thenReturn(dto);
        ResponseEntity<ServiceCalendarDTO> res = controller.getCalendarById(1L);
        assertEquals(HttpStatus.OK, res.getStatusCode());
    }

    @Test void testCreate() {
        when(calendarService.create(any(ServiceCalendarDTO.class))).thenReturn(dto);
        ResponseEntity<ServiceCalendarDTO> res = controller.createCalendar(dto);
        assertEquals(HttpStatus.CREATED, res.getStatusCode());
    }

    @Test void testUpdate() {
        when(calendarService.update(eq(1L), any(ServiceCalendarDTO.class))).thenReturn(dto);
        ResponseEntity<ServiceCalendarDTO> res = controller.updateCalendar(1L, dto);
        assertEquals(HttpStatus.OK, res.getStatusCode());
    }

    @Test void testDelete() {
        doNothing().when(calendarService).delete(1L);
        ResponseEntity<Void> res = controller.deleteCalendar(1L);
        assertEquals(HttpStatus.NO_CONTENT, res.getStatusCode());
    }
}
