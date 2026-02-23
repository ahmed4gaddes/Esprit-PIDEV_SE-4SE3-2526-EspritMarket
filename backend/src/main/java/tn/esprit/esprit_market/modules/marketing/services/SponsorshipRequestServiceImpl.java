package tn.esprit.esprit_market.modules.marketing.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.esprit_market.modules.marketing.entity.SponsorshipRequest;
import tn.esprit.esprit_market.modules.marketing.repository.SponsorshipRequestRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SponsorshipRequestServiceImpl implements ISponsorshipRequestService {

    private final SponsorshipRequestRepository requestRepository;

    @Override
    @Transactional
    public SponsorshipRequest add(SponsorshipRequest request) {
        // Les associations (user, sponsorship) sont gérées automatiquement
        return requestRepository.save(request);
    }

    @Override
    @Transactional
    public SponsorshipRequest update(SponsorshipRequest request) {
        return requestRepository.save(request);
    }

    @Override
    public SponsorshipRequest getById(Long id) {
        return requestRepository.findById(id).orElse(null);
    }

    @Override
    public List<SponsorshipRequest> getAll() {
        return requestRepository.findAll();
    }

    @Override
    public void delete(Long id) {
        requestRepository.deleteById(id);
    }

    @Override
    public List<SponsorshipRequest> getByState(String state) {

        return List.of();
    }

    @Override
    public List<SponsorshipRequest> getByUser(Long userId) {
        return List.of();
    }
}