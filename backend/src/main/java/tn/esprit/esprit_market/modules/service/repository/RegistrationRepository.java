package tn.esprit.esprit_market.modules.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.esprit_market.modules.service.entity.Registration;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {
    java.util.List<Registration> findByWorkshopId(Long workshopId);
    java.util.List<Registration> findByUserId(Long userId);
}