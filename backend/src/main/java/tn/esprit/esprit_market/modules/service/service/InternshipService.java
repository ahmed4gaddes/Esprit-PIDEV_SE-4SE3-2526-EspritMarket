package tn.esprit.esprit_market.modules.service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.esprit_market.modules.service.entity.Internship;
import tn.esprit.esprit_market.modules.service.repository.InternshipRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InternshipService {
    private final InternshipRepository internshipRepository;

    public List<Internship> getAll() {
        return internshipRepository.findAll();
    }

    public Internship getById(Long id) {
        return internshipRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Internship not found with id: " + id));
    }

    public Internship create(Internship internship) {
        return internshipRepository.save(internship);
    }

    public Internship update(Long id, Internship internship) {
        if (!internshipRepository.existsById(id)) {
            throw new RuntimeException("Cannot update: Internship not found with id: " + id);
        }
        internship.setId(id); // Ensure the ID from the URL is set on the object
        return internshipRepository.save(internship);
    }

    public void delete(Long id) {
        if (!internshipRepository.existsById(id)) {
            throw new RuntimeException("Cannot delete: Internship not found with id: " + id);
        }
        internshipRepository.deleteById(id);
    }
}