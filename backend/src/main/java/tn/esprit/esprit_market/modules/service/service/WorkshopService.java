package tn.esprit.esprit_market.modules.service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.esprit_market.modules.service.entity.Workshop;
import tn.esprit.esprit_market.modules.service.repository.WorkshopRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkshopService {
    private final WorkshopRepository workshopRepository;

    public List<Workshop> getAll() {
        return workshopRepository.findAll();
    }

    public Workshop getById(Long id) {
        return workshopRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Workshop not found with id: " + id));
    }

    public Workshop create(Workshop workshop) {
        return workshopRepository.save(workshop);
    }

    public Workshop update(Long id, Workshop workshop) {
        if (!workshopRepository.existsById(id)) {
            throw new RuntimeException("Workshop not found");
        }
        workshop.setId(id);
        return workshopRepository.save(workshop);
    }

    public void delete(Long id) {
        workshopRepository.deleteById(id);
    }
}