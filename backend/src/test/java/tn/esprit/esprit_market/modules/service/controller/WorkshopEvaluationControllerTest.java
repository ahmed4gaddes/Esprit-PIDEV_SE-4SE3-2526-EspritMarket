package tn.esprit.esprit_market.modules.service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import tn.esprit.esprit_market.modules.service.dto.WorkshopEvaluationDTO;
import tn.esprit.esprit_market.modules.service.service.IWorkshopEvaluationService;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkshopEvaluationControllerTest {

    @Mock private IWorkshopEvaluationService evaluationService;
    @InjectMocks private WorkshopEvaluationController controller;
    private WorkshopEvaluationDTO dto;

    @BeforeEach
    void setUp() { dto = new WorkshopEvaluationDTO(); dto.setId(1L); }

    @Test void testGetAll() {
        when(evaluationService.getAll()).thenReturn(Arrays.asList(dto));
        ResponseEntity<List<WorkshopEvaluationDTO>> res = controller.getAllEvaluations();
        assertEquals(HttpStatus.OK, res.getStatusCode());
    }

    @Test void testGetById() {
        when(evaluationService.getById(1L)).thenReturn(dto);
        ResponseEntity<WorkshopEvaluationDTO> res = controller.getEvaluationById(1L);
        assertEquals(HttpStatus.OK, res.getStatusCode());
    }

    @Test void testCreate() {
        when(evaluationService.create(any(WorkshopEvaluationDTO.class))).thenReturn(dto);
        ResponseEntity<WorkshopEvaluationDTO> res = controller.createEvaluation(dto);
        assertEquals(HttpStatus.CREATED, res.getStatusCode());
    }

    @Test void testUpdate() {
        when(evaluationService.update(eq(1L), any(WorkshopEvaluationDTO.class))).thenReturn(dto);
        ResponseEntity<WorkshopEvaluationDTO> res = controller.updateEvaluation(1L, dto);
        assertEquals(HttpStatus.OK, res.getStatusCode());
    }

    @Test void testDelete() {
        doNothing().when(evaluationService).delete(1L);
        ResponseEntity<Void> res = controller.deleteEvaluation(1L);
        assertEquals(HttpStatus.NO_CONTENT, res.getStatusCode());
    }
}
