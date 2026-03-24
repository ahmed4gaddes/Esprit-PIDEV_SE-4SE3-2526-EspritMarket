package tn.esprit.esprit_market.modules.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.esprit_market.modules.service.entity.SupportingDocument;

@Repository
public interface SupportingDocumentRepository extends JpaRepository<SupportingDocument, Long> {
    java.util.List<SupportingDocument> findByCertificateId(Long certificateId);
}