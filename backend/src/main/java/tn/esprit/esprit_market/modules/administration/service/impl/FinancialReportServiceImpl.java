package tn.esprit.esprit_market.modules.administration.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.esprit_market.exceptions.ResourceNotFoundException;
import tn.esprit.esprit_market.modules.administration.dto.FinancialReportDTO;
import tn.esprit.esprit_market.modules.administration.entity.Commission;
import tn.esprit.esprit_market.modules.administration.entity.FinancialReport;
import tn.esprit.esprit_market.modules.administration.mapper.AdministrationMapper;
import tn.esprit.esprit_market.modules.administration.repository.CommissionRepository;
import tn.esprit.esprit_market.modules.administration.repository.FinancialReportRepository;
import tn.esprit.esprit_market.modules.administration.service.IFinancialReportService;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FinancialReportServiceImpl implements IFinancialReportService {

    private final FinancialReportRepository financialReportRepository;
    private final CommissionRepository commissionRepository;
    private final AdministrationMapper administrationMapper;

    @Override
    @Transactional
    public FinancialReportDTO createFinancialReport(FinancialReportDTO dto) {
        Commission commission = null;
        if (dto.getCommissionId() != null) {
            commission = commissionRepository.findById(dto.getCommissionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Commission not found: " + dto.getCommissionId()));
        }

        if (dto.getDate() == null) {
            dto.setDate(new Date());
        }

        FinancialReport report = administrationMapper.toFinancialReportEntity(dto, commission);
        FinancialReport saved = financialReportRepository.save(report);
        return administrationMapper.toFinancialReportDTO(saved);
    }

    @Override
    @Transactional
    public FinancialReportDTO updateFinancialReport(Long id, FinancialReportDTO dto) {
        FinancialReport existing = financialReportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FinancialReport not found: " + id));

        if (dto.getCommissionId() != null) {
            Commission commission = commissionRepository.findById(dto.getCommissionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Commission not found: " + dto.getCommissionId()));
            existing.setCommission(commission);
        }

        existing.setRevenue(dto.getRevenue());
        existing.setCommissionAmount(dto.getCommissionAmount());
        existing.setNet(dto.getNet());
        if (dto.getDate() != null) {
            existing.setDate(dto.getDate());
        }

        FinancialReport saved = financialReportRepository.save(existing);
        return administrationMapper.toFinancialReportDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public FinancialReportDTO getFinancialReportById(Long id) {
        FinancialReport report = financialReportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FinancialReport not found: " + id));
        return administrationMapper.toFinancialReportDTO(report);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FinancialReportDTO> getReportsByCommission(Long commissionId) {
        return financialReportRepository.findByCommission_Id(commissionId).stream()
                .map(administrationMapper::toFinancialReportDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FinancialReportDTO> getAllFinancialReports() {
        return financialReportRepository.findAll().stream()
                .map(administrationMapper::toFinancialReportDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteFinancialReport(Long id) {
        if (!financialReportRepository.existsById(id)) {
            throw new ResourceNotFoundException("FinancialReport not found: " + id);
        }
        financialReportRepository.deleteById(id);
    }
}
