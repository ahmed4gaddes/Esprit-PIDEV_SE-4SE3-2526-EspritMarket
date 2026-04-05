package tn.esprit.esprit_market.modules.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.esprit_market.modules.event.entities.LiveSession;
import tn.esprit.esprit_market.modules.event.entities.Times;
import tn.esprit.esprit_market.modules.event.repositories.TimesRepository;
import tn.esprit.esprit_market.modules.event.repositories.LiveSessionRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TimesService {

    private final TimesRepository timesRepository;
    private final LiveSessionRepository liveSessionRepository;

    public Times addTimes(Long liveSessionId, Times times) {
        LiveSession liveSession = liveSessionRepository.findById(liveSessionId)
                .orElseThrow(() -> new RuntimeException("LiveSession not found with id: " + liveSessionId));
        times.setLiveSession(liveSession);
        return timesRepository.save(times);
    }

    public List<Times> getTimesByLiveSession(Long liveSessionId) {
        return timesRepository.findByLiveSessionId(liveSessionId);
    }

    public List<Times> getAll() {
        return timesRepository.findAll();
    }

    public Times getById(Long id) {
        return timesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Times not found with id: " + id));
    }

    public Times update(Long id, Times updated) {
        Times existing = timesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Times not found with id: " + id));
        existing.setType(updated.getType());
        existing.setStartTime(updated.getStartTime());
        existing.setEndTime(updated.getEndTime());
        existing.setDuration(updated.getDuration());
        existing.setDescription(updated.getDescription());
        return timesRepository.save(existing);
    }

    public void delete(Long id) {
        if (!timesRepository.existsById(id)) {
            throw new RuntimeException("Times not found with id: " + id);
        }
        timesRepository.deleteById(id);
    }
}
