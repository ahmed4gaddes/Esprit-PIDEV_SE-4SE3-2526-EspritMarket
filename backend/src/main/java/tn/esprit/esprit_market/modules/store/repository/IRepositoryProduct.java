package tn.esprit.esprit_market.modules.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.esprit_market.modules.store.entity.Product;

import java.util.Optional;
@Repository
public interface IRepositoryProduct extends JpaRepository<Product, Long> {

    boolean existsByNameIgnoreCase(String name);

    Optional<Product> findFirstByNameIgnoreCase(String name);
}
