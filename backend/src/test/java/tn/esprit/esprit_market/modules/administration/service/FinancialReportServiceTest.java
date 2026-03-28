package tn.esprit.esprit_market.modules.administration.service;

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
import tn.esprit.esprit_market.modules.administration.service.impl.FinancialReportServiceImpl;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FinancialReportServiceTest {

    @Mock
    private FinancialReportRepository financialReportRepository;

    @Mock
    private CommissionRepository commissionRepository;

    @Mock
    private AdministrationMapper administrationMapper;

    @InjectMocks
    private FinancialReportServiceImpl financialReportService;

    private FinancialReport report;
    private FinancialReportDTO reportDTO;
    private Commission commission;

    @BeforeEach
    void setUp() {
        commission = Commission.builder().id(10L).rate(5.0).amount(50.0).build();
        report = FinancialReport.builder().id(1L).revenue(1000.0).commissionAmount(50.0).net(950.0).date(new Date()).commission(commission).build();
        reportDTO = FinancialReportDTO.builder().id(1L).revenue(1000.0).commissionAmount(50.0).net(950.0).commissionId(10L).build();
    }

    @Test
    void createFinancialReport_ShouldReturnSavedReport() {
        when(commissionRepository.findById(10L)).thenReturn(Optional.of(commission));
        when(administrationMapper.toFinancialReportEntity(any(), any())).thenReturn(report);
        when(financialReportRepository.save(any())).thenReturn(report);
        when(administrationMapper.toFinancialReportDTO(any())).thenReturn(reportDTO);

        FinancialReportDTO result = financialReportService.createFinancialReport(reportDTO);

        assertNotNull(result);
        assertEquals(1000.0, result.getRevenue());
        assertEquals(950.0, result.getNet());
        assertEquals(10L, result.getCommissionId());
        verify(financialReportRepository, times(1)).save(any());
    }

    @Test
    void createFinancialReport_CommissionNotFound_ShouldThrowException() {
        when(commissionRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> financialReportService.createFinancialReport(reportDTO));
        verify(financialReportRepository, never()).save(any());
    }

    @Test
    void getFinancialReportById_ShouldReturnReport() {
        when(financialReportRepository.findById(1L)).thenReturn(Optional.of(report));
        when(administrationMapper.toFinancialReportDTO(report)).thenReturn(reportDTO);

        FinancialReportDTO result = financialReportService.getFinancialReportById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getReportsByCommission_ShouldReturnList() {
        when(financialReportRepository.findByCommission_Id(10L)).thenReturn(List.of(report));
        when(administrationMapper.toFinancialReportDTO(any())).thenReturn(reportDTO);

        List<FinancialReportDTO> result = financialReportService.getReportsByCommission(10L);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void getAllFinancialReports_ShouldReturnList() {
        when(financialReportRepository.findAll()).thenReturn(List.of(report));
        when(administrationMapper.toFinancialReportDTO(any())).thenReturn(reportDTO);

        List<FinancialReportDTO> list = financialReportService.getAllFinancialReports();

        assertFalse(list.isEmpty());
        assertEquals(1, list.size());
    }

    @Test
    void deleteFinancialReport_ShouldDeleteWhenExists() {
        when(financialReportRepository.existsById(1L)).thenReturn(true);
        doNothing().when(financialReportRepository).deleteById(1L);

        assertDoesNotThrow(() -> financialReportService.deleteFinancialReport(1L));
        verify(financialReportRepository, times(1)).deleteById(1L);
    }
}
