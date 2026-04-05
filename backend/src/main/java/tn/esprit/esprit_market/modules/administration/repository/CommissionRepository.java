package tn.esprit.esprit_market.modules.administration.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.esprit_market.modules.administration.entity.Commission;

import java.util.Optional;

@Repository
public interface CommissionRepository extends JpaRepository<Commission, Long> {
    Optional<Commission> findByStore_Id(Long storeId);
}
