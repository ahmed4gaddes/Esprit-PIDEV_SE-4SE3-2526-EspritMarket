package tn.esprit.esprit_market.modules.service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import tn.esprit.esprit_market.modules.service.dto.GamificationDTO;
import tn.esprit.esprit_market.modules.service.service.IGamificationService;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GamificationControllerTest {

    @Mock private IGamificationService gamificationService;
    @InjectMocks private GamificationController controller;
    private GamificationDTO dto;

    @BeforeEach
    void setUp() { dto = new GamificationDTO(); dto.setId(1L); }

    @Test void testGetAll() {
        when(gamificationService.getAll()).thenReturn(Arrays.asList(dto));
        ResponseEntity<List<GamificationDTO>> res = controller.getAllGamifications();
        assertEquals(HttpStatus.OK, res.getStatusCode());
    }

    @Test void testGetById() {
        when(gamificationService.getById(1L)).thenReturn(dto);
        ResponseEntity<GamificationDTO> res = controller.getGamificationById(1L);
        assertEquals(HttpStatus.OK, res.getStatusCode());
    }

    @Test void testGetByUserId() {
        when(gamificationService.getByUserId(1L)).thenReturn(dto);
        ResponseEntity<GamificationDTO> res = controller.getByUserId(1L);
        assertEquals(HttpStatus.OK, res.getStatusCode());
    }

    @Test void testCreate() {
        when(gamificationService.create(any(GamificationDTO.class))).thenReturn(dto);
        ResponseEntity<GamificationDTO> res = controller.createGamification(dto);
        assertEquals(HttpStatus.CREATED, res.getStatusCode());
    }

    @Test void testUpdate() {
        when(gamificationService.update(eq(1L), any(GamificationDTO.class))).thenReturn(dto);
        ResponseEntity<GamificationDTO> res = controller.updateGamification(1L, dto);
        assertEquals(HttpStatus.OK, res.getStatusCode());
    }

    @Test void testDelete() {
        doNothing().when(gamificationService).delete(1L);
        ResponseEntity<Void> res = controller.deleteGamification(1L);
        assertEquals(HttpStatus.NO_CONTENT, res.getStatusCode());
    }
}
