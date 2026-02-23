package tn.esprit.esprit_market.modules.marketing.repository;

import tn.esprit.esprit_market.modules.marketing.entity.Advertisement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdvertisementRepository extends JpaRepository<Advertisement, Long> {
}