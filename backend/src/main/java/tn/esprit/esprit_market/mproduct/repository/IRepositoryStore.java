package tn.esprit.esprit_market.mproduct.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.esprit_market.entities.User;
import tn.esprit.esprit_market.mproduct.entites.Store;

public interface IRepositoryStore extends JpaRepository<Store, Long> {
}
