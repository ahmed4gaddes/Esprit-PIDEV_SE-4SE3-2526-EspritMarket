package tn.esprit.esprit_market.modules.marketing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.esprit_market.modules.marketing.entity.Sponsorship;

import java.util.List;

@Repository
public interface SponsorshipRepository extends JpaRepository<Sponsorship, Long> {
    List<Sponsorship> findBySponsor_Id(Long sponsorId);
    List<Sponsorship> findByCampaign_Id(Long campaignId);
}
