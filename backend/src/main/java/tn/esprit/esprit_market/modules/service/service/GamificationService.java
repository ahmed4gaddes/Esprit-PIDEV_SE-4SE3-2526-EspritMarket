package tn.esprit.esprit_market.modules.service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.esprit_market.modules.service.entity.Gamification;
import tn.esprit.esprit_market.modules.service.repository.GamificationRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GamificationService {
    private final GamificationRepository gamificationRepository;

    public List<Gamification> getAll() {
        return gamificationRepository.findAll();
    }

    public Gamification getById(Long id) {
        return gamificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Gamification data not found"));
    }

    public Gamification create(Gamification gamification) {
        return gamificationRepository.save(gamification);
    }

    public Gamification update(Long id, Gamification gamification) {
        if (!gamificationRepository.existsById(id)) {
            throw new RuntimeException("Gamification record not found");
        }
        gamification.setId(id);
        return gamificationRepository.save(gamification);
    }

    public void delete(Long id) {
        gamificationRepository.deleteById(id);
    }
}