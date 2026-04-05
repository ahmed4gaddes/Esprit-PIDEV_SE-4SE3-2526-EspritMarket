package tn.esprit.esprit_market.modules.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.esprit_market.modules.admin.entity.AdminAuditLog;

@Repository
public interface AdminAuditLogRepository extends JpaRepository<AdminAuditLog, Long> {
}
