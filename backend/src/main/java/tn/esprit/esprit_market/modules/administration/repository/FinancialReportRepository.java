package tn.esprit.esprit_market.modules.administration.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.esprit_market.modules.administration.entity.FinancialReport;

import java.util.List;

@Repository
public interface FinancialReportRepository extends JpaRepository<FinancialReport, Long> {
    List<FinancialReport> findByCommission_Id(Long commissionId);
}
