package tn.esprit.esprit_market.modules.administration.mapper;

import org.springframework.stereotype.Component;
import tn.esprit.esprit_market.modules.administration.dto.CommissionDTO;
import tn.esprit.esprit_market.modules.administration.dto.FinancialReportDTO;
import tn.esprit.esprit_market.modules.administration.dto.RuleDTO;
import tn.esprit.esprit_market.modules.administration.entity.Commission;
import tn.esprit.esprit_market.modules.administration.entity.FinancialReport;
import tn.esprit.esprit_market.modules.administration.entity.Rule;
import tn.esprit.esprit_market.modules.store.entity.Store;
import tn.esprit.esprit_market.modules.user.entity.User;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class AdministrationMapper {

    public RuleDTO toRuleDTO(Rule rule) {
        if (rule == null) return null;

        return RuleDTO.builder()
                .id(rule.getId())
                .title(rule.getTitle())
                .text(rule.getText())
                .category(rule.getCategory())
                .active(rule.isActive())
                .mandatory(rule.isMandatory())
                .createdAt(rule.getCreatedAt())
                .updatedAt(rule.getUpdatedAt())
                .createdById(rule.getCreatedBy() != null ? rule.getCreatedBy().getId() : null)
                .createdByName(rule.getCreatedBy() != null ? rule.getCreatedBy().getName() : null)
                .appliesToStoreIds(rule.getAppliesTo() != null ? 
                        rule.getAppliesTo().stream().map(Store::getId).collect(Collectors.toSet()) : 
                        new java.util.HashSet<>())
                .build();
    }

    public Rule toRuleEntity(RuleDTO dto, User createdBy, Set<Store> appliesToStores) {
        if (dto == null) return null;

        return Rule.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .text(dto.getText())
                .category(dto.getCategory())
                .active(dto.isActive())
                .mandatory(dto.isMandatory())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .createdBy(createdBy)
                .appliesTo(appliesToStores != null ? appliesToStores : new java.util.HashSet<>())
                .build();
    }

    public CommissionDTO toCommissionDTO(Commission commission) {
        if (commission == null) return null;

        return CommissionDTO.builder()
                .id(commission.getId())
                .rate(commission.getRate())
                .amount(commission.getAmount())
                .storeId(commission.getStore() != null ? commission.getStore().getId() : null)
                .storeName(commission.getStore() != null ? commission.getStore().getName() : null)
                .build();
    }

    public Commission toCommissionEntity(CommissionDTO dto, Store store) {
        if (dto == null) return null;

        return Commission.builder()
                .id(dto.getId())
                .rate(dto.getRate())
                .amount(dto.getAmount())
                .store(store)
                .build();
    }

    public FinancialReportDTO toFinancialReportDTO(FinancialReport report) {
        if (report == null) return null;

        return FinancialReportDTO.builder()
                .id(report.getId())
                .revenue(report.getRevenue())
                .commissionAmount(report.getCommissionAmount())
                .net(report.getNet())
                .date(report.getDate())
                .commissionId(report.getCommission() != null ? report.getCommission().getId() : null)
                .storeName((report.getCommission() != null && report.getCommission().getStore() != null) 
                        ? report.getCommission().getStore().getName() : null)
                .build();
    }

    public FinancialReport toFinancialReportEntity(FinancialReportDTO dto, Commission commission) {
        if (dto == null) return null;

        return FinancialReport.builder()
                .id(dto.getId())
                .revenue(dto.getRevenue())
                .commissionAmount(dto.getCommissionAmount())
                .net(dto.getNet())
                .date(dto.getDate())
                .commission(commission)
                .build();
    }
}
