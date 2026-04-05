package tn.esprit.esprit_market.modules.marketing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.esprit_market.modules.marketing.entity.SponsorshipRequest;

import java.util.List;

@Repository
public interface SponsorshipRequestRepository extends JpaRepository<SponsorshipRequest, Long> {
    List<SponsorshipRequest> findByCompany_IdOrderByDateDesc(Long companyId);
    List<SponsorshipRequest> findBySponsor_IdOrderByDateDesc(Long sponsorId);
    List<SponsorshipRequest> findByStateOrderByDateDesc(String state);
    List<SponsorshipRequest> findByStateAndSponsorDesignUrlIsNotNullOrderByDateDesc(String state);
}

