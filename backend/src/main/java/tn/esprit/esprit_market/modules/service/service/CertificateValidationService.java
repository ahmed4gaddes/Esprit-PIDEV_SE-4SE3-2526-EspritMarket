package tn.esprit.esprit_market.modules.service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.esprit_market.modules.service.entity.CertificateValidation;
import tn.esprit.esprit_market.modules.service.repository.CertificateValidationRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CertificateValidationService {
    private final CertificateValidationRepository validationRepository;

    public List<CertificateValidation> getAll() {
        return validationRepository.findAll();
    }

    public CertificateValidation getById(Long id) {
        return validationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Validation record not found"));
    }

    public CertificateValidation create(CertificateValidation validation) {
        return validationRepository.save(validation);
    }

    public CertificateValidation update(Long id, CertificateValidation validation) {
        if (!validationRepository.existsById(id)) {
            throw new RuntimeException("Validation record not found");
        }
        validation.setId(id);
        return validationRepository.save(validation);
    }

    public void delete(Long id) {
        validationRepository.deleteById(id);
    }
}