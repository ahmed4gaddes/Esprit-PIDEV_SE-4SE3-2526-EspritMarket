package tn.esprit.esprit_market.modules.administration.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import tn.esprit.esprit_market.modules.administration.dto.FinancialReportDTO;
import tn.esprit.esprit_market.modules.administration.service.IFinancialReportService;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FinancialReportControllerTest {

    @Mock private IFinancialReportService financialReportService;
    @InjectMocks private FinancialReportController controller;
    private FinancialReportDTO dto;

    @BeforeEach
    void setUp() { dto = FinancialReportDTO.builder().id(1L).build(); }

    @Test void testCreate() {
        when(financialReportService.createFinancialReport(any(FinancialReportDTO.class))).thenReturn(dto);
        ResponseEntity<FinancialReportDTO> res = controller.createFinancialReport(dto);
        assertEquals(HttpStatus.CREATED, res.getStatusCode());
    }

    @Test void testUpdate() {
        when(financialReportService.updateFinancialReport(eq(1L), any(FinancialReportDTO.class))).thenReturn(dto);
        ResponseEntity<FinancialReportDTO> res = controller.updateFinancialReport(1L, dto);
        assertEquals(HttpStatus.OK, res.getStatusCode());
    }

    @Test void testGetById() {
        when(financialReportService.getFinancialReportById(1L)).thenReturn(dto);
        ResponseEntity<FinancialReportDTO> res = controller.getFinancialReportById(1L);
        assertEquals(HttpStatus.OK, res.getStatusCode());
    }

    @Test void testGetByCommission() {
        when(financialReportService.getReportsByCommission(1L)).thenReturn(Arrays.asList(dto));
        ResponseEntity<List<FinancialReportDTO>> res = controller.getReportsByCommission(1L);
        assertEquals(HttpStatus.OK, res.getStatusCode());
    }

    @Test void testGetAll() {
        when(financialReportService.getAllFinancialReports()).thenReturn(Arrays.asList(dto));
        ResponseEntity<List<FinancialReportDTO>> res = controller.getAllFinancialReports();
        assertEquals(HttpStatus.OK, res.getStatusCode());
    }

    @Test void testDelete() {
        doNothing().when(financialReportService).deleteFinancialReport(1L);
        ResponseEntity<Void> res = controller.deleteFinancialReport(1L);
        assertEquals(HttpStatus.NO_CONTENT, res.getStatusCode());
    }
}
