package tn.esprit.esprit_market.modules.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.esprit_market.modules.service.entity.CertificateValidation;

@Repository
public interface CertificateValidationRepository extends JpaRepository<CertificateValidation, Long> {
    java.util.List<CertificateValidation> findByCertificateId(Long certificateId);
}