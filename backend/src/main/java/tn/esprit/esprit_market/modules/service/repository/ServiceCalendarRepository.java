package tn.esprit.esprit_market.modules.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.esprit_market.modules.service.entity.ServiceCalendar;

@Repository
public interface ServiceCalendarRepository extends JpaRepository<ServiceCalendar, Long> {
    java.util.List<ServiceCalendar> findByServiceId(Long serviceId);
}