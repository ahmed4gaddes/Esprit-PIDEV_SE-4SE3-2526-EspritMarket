package tn.esprit.esprit_market.modules.administration.service;

import tn.esprit.esprit_market.modules.administration.dto.FinancialReportDTO;

import java.util.List;

public interface IFinancialReportService {
    FinancialReportDTO createFinancialReport(FinancialReportDTO dto);
    FinancialReportDTO updateFinancialReport(Long id, FinancialReportDTO dto);
    FinancialReportDTO getFinancialReportById(Long id);
    List<FinancialReportDTO> getReportsByCommission(Long commissionId);
    List<FinancialReportDTO> getAllFinancialReports();
    void deleteFinancialReport(Long id);
}
