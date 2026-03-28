package tn.esprit.esprit_market.modules.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.esprit_market.modules.user.entity.Rate;

import java.util.List;
import java.util.Optional;

@Repository
public interface RateRepository extends JpaRepository<Rate, Long> {
    List<Rate> findByRatedUser_Id(Long ratedUserId);
    List<Rate> findByRater_Id(Long raterId);
    Optional<Rate> findByRater_IdAndRatedUser_Id(Long raterId, Long ratedUserId);
    boolean existsByRater_IdAndRatedUser_Id(Long raterId, Long ratedUserId);
}
