package tn.esprit.esprit_market.modules.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.esprit_market.modules.user.entity.Rate;
import tn.esprit.esprit_market.modules.user.entity.User;
import tn.esprit.esprit_market.modules.user.repository.RateRepository;
import tn.esprit.esprit_market.modules.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RateService {

    private final RateRepository rateRepository;
    private final UserRepository userRepository;

    public Rate addRate(Long raterId, Long ratedUserId, Rate rate) {
        if (raterId.equals(ratedUserId)) {
            throw new RuntimeException("A user cannot rate themselves.");
        }
        User rater = userRepository.findById(raterId)
                .orElseThrow(() -> new RuntimeException("Rater not found with id: " + raterId));
        User ratedUser = userRepository.findById(ratedUserId)
                .orElseThrow(() -> new RuntimeException("Rated user not found with id: " + ratedUserId));

        return rateRepository.findByRater_IdAndRatedUser_Id(raterId, ratedUserId)
                .map(existing -> {
                    existing.setStar(rate.getStar());
                    existing.setComment(rate.getComment());
                    return rateRepository.save(existing);
                })
                .orElseGet(() -> {
                    rate.setRater(rater);
                    rate.setRatedUser(ratedUser);
                    return rateRepository.save(rate);
                });
    }

    public List<Rate> getRatesForUser(Long ratedUserId) {
        return rateRepository.findByRatedUser_Id(ratedUserId);
    }

    public List<Rate> getRatesByRater(Long raterId) {
        return rateRepository.findByRater_Id(raterId);
    }

    public double getAverageRating(Long ratedUserId) {
        List<Rate> rates = rateRepository.findByRatedUser_Id(ratedUserId);
        if (rates.isEmpty()) return 0.0;
        return rates.stream().mapToInt(Rate::getStar).average().orElse(0.0);
    }

    public Rate updateRate(Long id, Rate updated) {
        Rate existing = rateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rate not found with id: " + id));
        existing.setStar(updated.getStar());
        existing.setComment(updated.getComment());
        return rateRepository.save(existing);
    }

    public void deleteRate(Long id) {
        if (!rateRepository.existsById(id)) {
            throw new RuntimeException("Rate not found with id: " + id);
        }
        rateRepository.deleteById(id);
    }

    public Rate getById(Long id) {
        return rateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rate not found with id: " + id));
    }
}
