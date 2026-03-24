package tn.esprit.esprit_market.modules.service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.esprit_market.exceptions.ResourceNotFoundException;
import tn.esprit.esprit_market.modules.service.dto.ServiceDTO;
import tn.esprit.esprit_market.modules.service.mapper.ServiceModuleMapper;
import tn.esprit.esprit_market.modules.service.repository.ServiceRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ServiceService implements IServiceService {
    private final ServiceRepository serviceRepository;
    private final ServiceModuleMapper mapper;

    @Transactional(readOnly = true)
    public List<ServiceDTO> getAll() {
        return serviceRepository.findAll()
                .stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ServiceDTO getById(Long id) {
        tn.esprit.esprit_market.modules.service.entity.Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with id: " + id));
        return mapper.toDto(service);
    }

    public void delete(Long id) {
        if (!serviceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cannot delete: Service not found with id: " + id);
        }
        serviceRepository.deleteById(id);
    }

    // Since Service is the base entity, we don't usually CREATE it directly here
    // But we might need update
    public ServiceDTO update(Long id, ServiceDTO dto) {
        tn.esprit.esprit_market.modules.service.entity.Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with id: " + id));

        mapper.toEntity(dto, service);
        return mapper.toDto(serviceRepository.save(service));
    }
}