package tn.esprit.esprit_market.modules.administration.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import tn.esprit.esprit_market.modules.administration.dto.CommissionDTO;
import tn.esprit.esprit_market.modules.administration.service.ICommissionService;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommissionControllerTest {

    @Mock
    private ICommissionService commissionService;

    @InjectMocks
    private CommissionController controller;

    private CommissionDTO dto;

    @BeforeEach
    void setUp() {
        dto = new CommissionDTO();
        dto.setId(10L);
        dto.setRate(5.0);
        dto.setAmount(100.0);
    }

    @Test
    void testCreateCommission() {
        when(commissionService.createCommission(any(CommissionDTO.class))).thenReturn(dto);

        ResponseEntity<CommissionDTO> response = controller.createCommission(dto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(10L, response.getBody().getId());
        verify(commissionService).createCommission(any(CommissionDTO.class));
    }

    @Test
    void testUpdateCommission() {
        when(commissionService.updateCommission(eq(10L), any(CommissionDTO.class))).thenReturn(dto);

        ResponseEntity<CommissionDTO> response = controller.updateCommission(10L, dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(5.0, response.getBody().getRate());
        verify(commissionService).updateCommission(eq(10L), any(CommissionDTO.class));
    }

    @Test
    void testGetCommissionById() {
        when(commissionService.getCommissionById(10L)).thenReturn(dto);

        ResponseEntity<CommissionDTO> response = controller.getCommissionById(10L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(10L, response.getBody().getId());
        verify(commissionService).getCommissionById(10L);
    }

    @Test
    void testGetCommissionByStore() {
        when(commissionService.getCommissionByStore(20L)).thenReturn(dto);

        ResponseEntity<CommissionDTO> response = controller.getCommissionByStore(20L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(10L, response.getBody().getId());
        verify(commissionService).getCommissionByStore(20L);
    }

    @Test
    void testGetAllCommissions() {
        when(commissionService.getAllCommissions()).thenReturn(Arrays.asList(dto));

        ResponseEntity<List<CommissionDTO>> response = controller.getAllCommissions();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(commissionService).getAllCommissions();
    }

    @Test
    void testDeleteCommission() {
        doNothing().when(commissionService).deleteCommission(10L);

        ResponseEntity<Void> response = controller.deleteCommission(10L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(commissionService).deleteCommission(10L);
    }
}
