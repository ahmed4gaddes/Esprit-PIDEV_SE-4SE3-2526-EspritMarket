package tn.esprit.esprit_market.modules.event.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.esprit_market.modules.event.entities.Times;

import java.util.List;

@Repository
public interface TimesRepository extends JpaRepository<Times, Long> {
    List<Times> findByLiveSessionId(Long liveSessionId);
    List<Times> findByType(String type);
}
