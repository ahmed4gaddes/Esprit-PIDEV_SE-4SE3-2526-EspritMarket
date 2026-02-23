package tn.esprit.esprit_market.modules.marketing.services;

import tn.esprit.esprit_market.modules.marketing.entity.Sponsorship;
import tn.esprit.esprit_market.modules.marketing.enums.SponsorshipStatus;
import java.util.List;

public interface ISponsorshipService {
    Sponsorship add(Sponsorship sponsorship);
    Sponsorship update(Sponsorship sponsorship);
    Sponsorship getById(Long id);
    List<Sponsorship> getAll();
    void delete(Long id);

    List<Sponsorship> getByStatus(SponsorshipStatus status);
    List<Sponsorship> getBySponsor(Long sponsorId);
}