package tn.esprit.esprit_market.modules.service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.esprit_market.modules.service.entity.Certificate;
import tn.esprit.esprit_market.modules.service.repository.CertificateRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CertificateService {
    private final CertificateRepository certificateRepository;

    public List<Certificate> getAll() {
        return certificateRepository.findAll();
    }

    public Certificate getById(Long id) {
        return certificateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Certificate not found"));
    }

    public Certificate create(Certificate certificate) {
        return certificateRepository.save(certificate);
    }

    public Certificate update(Long id, Certificate certificate) {
        if (!certificateRepository.existsById(id)) {
            throw new RuntimeException("Certificate not found");
        }
        certificate.setId(id);
        return certificateRepository.save(certificate);
    }

    public void delete(Long id) {
        certificateRepository.deleteById(id);
    }
}