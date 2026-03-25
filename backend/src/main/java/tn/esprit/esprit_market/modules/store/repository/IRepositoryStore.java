package tn.esprit.esprit_market.modules.store.repository;

import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.esprit_market.modules.store.entity.Store;
@ReadingConverter
public interface IRepositoryStore extends JpaRepository<Store, Long> {
    java.util.List<Store> findByOwnerEmail(String email);
}
