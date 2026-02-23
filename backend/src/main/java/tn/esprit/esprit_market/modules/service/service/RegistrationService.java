package tn.esprit.esprit_market.modules.service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.esprit_market.modules.service.entity.Registration;
import tn.esprit.esprit_market.modules.service.repository.RegistrationRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RegistrationService {
    private final RegistrationRepository registrationRepository;

    public List<Registration> getAll() {
        return registrationRepository.findAll();
    }

    public Registration getById(Long id) {
        return registrationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registration not found with id: " + id));
    }

    public Registration create(Registration registration) {
        return registrationRepository.save(registration);
    }

    public Registration update(Long id, Registration registration) {
        if (!registrationRepository.existsById(id)) {
            throw new RuntimeException("Registration not found");
        }
        registration.setId(id);
        return registrationRepository.save(registration);
    }

    public void delete(Long id) {
        registrationRepository.deleteById(id);
    }
}