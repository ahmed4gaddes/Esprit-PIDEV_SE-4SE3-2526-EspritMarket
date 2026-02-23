package tn.esprit.esprit_market.modules.marketing.services;

import tn.esprit.esprit_market.modules.marketing.entity.SponsorshipRequest;
import java.util.List;

public interface ISponsorshipRequestService {
    SponsorshipRequest add(SponsorshipRequest request);
    SponsorshipRequest update(SponsorshipRequest request);
    SponsorshipRequest getById(Long id);
    List<SponsorshipRequest> getAll();
    void delete(Long id);

    List<SponsorshipRequest> getByState(String state);
    List<SponsorshipRequest> getByUser(Long userId);
}