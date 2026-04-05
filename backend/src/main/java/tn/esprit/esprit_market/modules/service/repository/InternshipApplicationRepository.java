package tn.esprit.esprit_market.modules.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.esprit_market.modules.service.entity.InternshipApplication;

import java.util.List;

public interface InternshipApplicationRepository extends JpaRepository<InternshipApplication, Long> {
    boolean existsByInternship_IdAndApplicant_Id(Long internshipId, Long applicantId);
    List<InternshipApplication> findByApplicant_IdOrderByAppliedAtDesc(Long applicantId);
    List<InternshipApplication> findByInternship_IdOrderByAppliedAtDesc(Long internshipId);
    void deleteByInternship_Id(Long internshipId);
}
