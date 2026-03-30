package tn.esprit.esprit_market.modules.administration.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.esprit_market.exceptions.ResourceNotFoundException;
import tn.esprit.esprit_market.modules.administration.dto.FinancialReportDTO;
import tn.esprit.esprit_market.modules.administration.entity.Commission;
import tn.esprit.esprit_market.modules.administration.entity.FinancialReport;
import tn.esprit.esprit_market.modules.administration.mapper.AdministrationMapper;
import tn.esprit.esprit_market.modules.administration.repository.CommissionRepository;
import tn.esprit.esprit_market.modules.administration.repository.FinancialReportRepository;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FinancialReportServiceImplTest {

    @Mock private FinancialReportRepository financialReportRepository;
    @Mock private CommissionRepository commissionRepository;
    @Mock private AdministrationMapper administrationMapper;

    @InjectMocks private FinancialReportServiceImpl reportService;

    private FinancialReportDTO dto;
    private FinancialReport entity;
    private Commission commission;

    @BeforeEach
    void setUp() {
        dto = new FinancialReportDTO();
        dto.setId(1L);
        dto.setCommissionId(2L);
        dto.setRevenue(100.0);
        dto.setCommissionAmount(10.0);
        dto.setNet(90.0);

        entity = new FinancialReport();
        entity.setId(1L);

        commission = new Commission();
        commission.setId(2L);
    }

    @Test
    void testCreateFinancialReport_Success() {
        when(commissionRepository.findById(2L)).thenReturn(Optional.of(commission));
        when(administrationMapper.toFinancialReportEntity(dto, commission)).thenReturn(entity);
        when(financialReportRepository.save(entity)).thenReturn(entity);
        when(administrationMapper.toFinancialReportDTO(entity)).thenReturn(dto);

        FinancialReportDTO res = reportService.createFinancialReport(dto);
        assertNotNull(res);
        assertNotNull(dto.getDate());
    }

    @Test
    void testCreateFinancialReport_NoCommissionId() {
        dto.setCommissionId(null);
        when(administrationMapper.toFinancialReportEntity(dto, null)).thenReturn(entity);
        when(financialReportRepository.save(entity)).thenReturn(entity);
        when(administrationMapper.toFinancialReportDTO(entity)).thenReturn(dto);

        FinancialReportDTO res = reportService.createFinancialReport(dto);
        assertNotNull(res);
    }

    @Test
    void testCreateFinancialReport_CommissionNotFound() {
        when(commissionRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> reportService.createFinancialReport(dto));
    }

    @Test
    void testUpdateFinancialReport_Success() {
        when(financialReportRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(commissionRepository.findById(2L)).thenReturn(Optional.of(commission));
        when(financialReportRepository.save(entity)).thenReturn(entity);
        when(administrationMapper.toFinancialReportDTO(entity)).thenReturn(dto);

        dto.setDate(new Date());
        FinancialReportDTO res = reportService.updateFinancialReport(1L, dto);
        assertNotNull(res);
    }

    @Test
    void testUpdateFinancialReport_NotFound() {
        when(financialReportRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> reportService.updateFinancialReport(1L, dto));
    }

    @Test
    void testGetFinancialReportById_Success() {
        when(financialReportRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(administrationMapper.toFinancialReportDTO(entity)).thenReturn(dto);

        assertNotNull(reportService.getFinancialReportById(1L));
    }

    @Test
    void testGetReportsByCommission() {
        when(financialReportRepository.findByCommission_Id(2L)).thenReturn(Arrays.asList(entity));
        when(administrationMapper.toFinancialReportDTO(entity)).thenReturn(dto);

        List<FinancialReportDTO> list = reportService.getReportsByCommission(2L);
        assertEquals(1, list.size());
    }

    @Test
    void testGetAllFinancialReports() {
        when(financialReportRepository.findAll()).thenReturn(Arrays.asList(entity));
        when(administrationMapper.toFinancialReportDTO(entity)).thenReturn(dto);

        assertEquals(1, reportService.getAllFinancialReports().size());
    }

    @Test
    void testDeleteFinancialReport_Success() {
        when(financialReportRepository.existsById(1L)).thenReturn(true);
        doNothing().when(financialReportRepository).deleteById(1L);

        assertDoesNotThrow(() -> reportService.deleteFinancialReport(1L));
    }

    @Test
    void testDeleteFinancialReport_NotFound() {
        when(financialReportRepository.existsById(1L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> reportService.deleteFinancialReport(1L));
    }
}
