package tn.esprit.esprit_market.modules.event.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import tn.esprit.esprit_market.modules.event.entities.Times;
import tn.esprit.esprit_market.modules.event.service.TimesService;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TimesControllerTest {

    @Mock private TimesService timesService;
    @InjectMocks private TimesController controller;
    private Times times;

    @BeforeEach
    void setUp() { times = new Times(); times.setId(1L); }

    @Test void testAdd() {
        when(timesService.addTimes(eq(1L), any(Times.class))).thenReturn(times);
        ResponseEntity<Times> res = controller.addTimes(1L, times);
        assertEquals(HttpStatus.CREATED, res.getStatusCode());
    }

    @Test void testGetAll() {
        when(timesService.getAll()).thenReturn(Arrays.asList(times));
        ResponseEntity<List<Times>> res = controller.getAll();
        assertEquals(HttpStatus.OK, res.getStatusCode());
    }

    @Test void testGetById() {
        when(timesService.getById(1L)).thenReturn(times);
        ResponseEntity<Times> res = controller.getById(1L);
        assertEquals(HttpStatus.OK, res.getStatusCode());
    }

    @Test void testGetByLiveSession() {
        when(timesService.getTimesByLiveSession(1L)).thenReturn(Arrays.asList(times));
        ResponseEntity<List<Times>> res = controller.getByLiveSession(1L);
        assertEquals(HttpStatus.OK, res.getStatusCode());
    }

    @Test void testUpdate() {
        when(timesService.update(eq(1L), any(Times.class))).thenReturn(times);
        ResponseEntity<Times> res = controller.update(1L, times);
        assertEquals(HttpStatus.OK, res.getStatusCode());
    }

    @Test void testDelete() {
        doNothing().when(timesService).delete(1L);
        ResponseEntity<Void> res = controller.delete(1L);
        assertEquals(HttpStatus.NO_CONTENT, res.getStatusCode());
    }
}
