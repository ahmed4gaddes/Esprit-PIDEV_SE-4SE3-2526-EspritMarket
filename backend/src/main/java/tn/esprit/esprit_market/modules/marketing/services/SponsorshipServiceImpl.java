package tn.esprit.esprit_market.modules.marketing.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.esprit_market.modules.marketing.entity.Sponsorship;
import tn.esprit.esprit_market.modules.marketing.enums.SponsorshipStatus;
import tn.esprit.esprit_market.modules.marketing.repository.SponsorshipRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SponsorshipServiceImpl implements ISponsorshipService {

    private final SponsorshipRepository sponsorshipRepository;

    @Override
    @Transactional
    public Sponsorship add(Sponsorship sponsorship) {

        return sponsorshipRepository.save(sponsorship);
    }

    @Override
    @Transactional
    public Sponsorship update(Sponsorship sponsorship) {
        return sponsorshipRepository.save(sponsorship);
    }

    @Override
    public Sponsorship getById(Long id) {
        return sponsorshipRepository.findById(id).orElse(null);
    }

    @Override
    public List<Sponsorship> getAll() {
        return sponsorshipRepository.findAll();
    }

    @Override
    public void delete(Long id) {
        sponsorshipRepository.deleteById(id);
    }

    @Override
    public List<Sponsorship> getByStatus(SponsorshipStatus status) {
        return List.of();
    }

    @Override
    public List<Sponsorship> getBySponsor(Long sponsorId) {
        return List.of();
    }
}