package tn.esprit.esprit_market.mproduct.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.esprit_market.entities.User;
import tn.esprit.esprit_market.mproduct.entites.ProductImage;

public interface IRepositoryProductImage extends JpaRepository<ProductImage, Long> {
}
