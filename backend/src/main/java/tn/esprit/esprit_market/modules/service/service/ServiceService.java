package tn.esprit.esprit_market.modules.service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.esprit_market.modules.service.repository.ServiceRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceService {
    private final ServiceRepository serviceRepository;

    public List<tn.esprit.esprit_market.modules.service.entity.Service> getAll() {
        return serviceRepository.findAll();
    }

    public tn.esprit.esprit_market.modules.service.entity.Service getById(Long id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found"));
    }

    public void delete(Long id) {
        serviceRepository.deleteById(id);
    }

    // Parent update logic if needed
    public tn.esprit.esprit_market.modules.service.entity.Service update(Long id, tn.esprit.esprit_market.modules.service.entity.Service service) {
        if (!serviceRepository.existsById(id)) {
            throw new RuntimeException("Service not found");
        }
        service.setId(id);
        return serviceRepository.save(service);
    }
}