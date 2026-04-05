package tn.esprit.esprit_market.modules.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.esprit_market.modules.store.entity.Category;
import java.util.List;

@Repository
public interface IRepositoryCategory extends JpaRepository<Category, Long> {
    List<Category> findByStoreId(Long storeId);
}
