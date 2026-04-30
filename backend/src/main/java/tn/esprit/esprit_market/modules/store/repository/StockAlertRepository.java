package tn.esprit.esprit_market.modules.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.esprit_market.modules.store.entity.StockAlert;

import java.util.List;

@Repository
public interface StockAlertRepository extends JpaRepository<StockAlert, Long> {
    List<StockAlert> findByProductIdAndNotifiedFalse(Long productId);
    boolean existsByUserIdAndProductIdAndNotifiedFalse(Long userId, Long productId);
}
