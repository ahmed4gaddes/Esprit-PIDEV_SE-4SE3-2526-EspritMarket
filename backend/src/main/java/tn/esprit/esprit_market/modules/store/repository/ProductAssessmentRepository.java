package tn.esprit.esprit_market.modules.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tn.esprit.esprit_market.modules.store.entity.ProductAssessment;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductAssessmentRepository extends JpaRepository<ProductAssessment, Long> {
    List<ProductAssessment> findByProduct_Id(Long productId);
    List<ProductAssessment> findByUser_Id(Long userId);
    Optional<ProductAssessment> findByUser_IdAndProduct_Id(Long userId, Long productId);
    long countByProduct_Id(Long productId);

    @Query("select avg(a.star) from ProductAssessment a where a.product.id = :productId")
    Double findAverageStarByProduct_Id(Long productId);
}
